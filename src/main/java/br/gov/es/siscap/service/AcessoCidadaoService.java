package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.acessocidadaoapi.LoginACResponseDto;
import br.gov.es.siscap.dto.acessocidadaoapi.SubResponseDto;
import br.gov.es.siscap.exception.ApiAcessoCidadaoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AcessoCidadaoService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String STATUS = "Status: ";

    @Value("${api.acessocidadao.uri.token}")
    private String acessocidadaoUriToken;
    @Value("${api.acessocidadao.uri.webapi}")
    private String acessocidadaoUriWebApi;
    @Value("${api.acessocidadao.client-id}")
    private String clientId;
    @Value("${api.acessocidadao.client-secret}")
    private String clientSecret;
    @Value("${api.acessocidadao.grant_type}")
    private String grantType;
    @Value("${api.acessocidadao.scope}")
    private String scope;
    private String clientToken;
    private LocalDateTime expiracaoClientToken;

    private final Logger logger = LogManager.getLogger(AcessoCidadaoService.class);

    private String getClientToken() {
        if (clientToken == null || expiracaoClientToken.isBefore(LocalDateTime.now()))
            obterClientToken();
        return clientToken;
    }

    private void obterClientToken() {
        final String basicToken = clientId + ":" + clientSecret;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", grantType);
        parameters.put("scope", scope);

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder(URI.create(acessocidadaoUriToken))
                .header(AUTHORIZATION,"Basic " + Base64.getEncoder()
                                .encodeToString(basicToken.getBytes(StandardCharsets.UTF_8)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                LoginACResponseDto loginACResponseDto = new ObjectMapper()
                        .readValue(response.body(), LoginACResponseDto.class);
                clientToken = loginACResponseDto.accessToken();
                expiracaoClientToken = LocalDateTime.now().plusHours(loginACResponseDto.expiresIn());
            } else {
                logger.error("Não foi possível gerar o ClientToken para a API do Acesso Cidadão");
                throw new ApiAcessoCidadaoException(Collections.singletonList(STATUS + response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage());
            throw new ApiAcessoCidadaoException(Collections.singletonList("Erro no fluxo de autenticação"));
        }
    }

    private String buscarSubPorCpf(String cpf) {
        String accessToken = getClientToken();

        String url = acessocidadaoUriWebApi.concat("/api/cidadao/" + cpf + "/pesquisaSub");

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header(AUTHORIZATION, BEARER + accessToken)
                .PUT(HttpRequest.BodyPublishers.ofString(cpf)).build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                SubResponseDto subResponseDto = new ObjectMapper().readValue(response.body(), SubResponseDto.class);
                return subResponseDto.sub();
            }
            logger.error("Não foi possível buscar o sub por cpf");
            throw new ApiAcessoCidadaoException(Collections.singletonList(STATUS + response.statusCode()));
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage());
            throw new ApiAcessoCidadaoException(Collections.singletonList("Erro ao buscar sub por cpf."));
        }
    }

    private AgentePublicoACDto buscarAgentePublicoPorSub(String sub) {
        String accessToken = getClientToken();
        String url = acessocidadaoUriWebApi.concat("/api/agentepublico/" + sub);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header(AUTHORIZATION, BEARER + accessToken)
                .GET().build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                var agentePublicoACResponseDto = new ObjectMapper()
                        .readValue(response.body(), AgentePublicoACDto.AgentePublicoACResponseDto.class);
                logger.info(agentePublicoACResponseDto);
                return new AgentePublicoACDto(agentePublicoACResponseDto);
            }
            logger.error("Não foi possível buscar o agente publico por sub");
            throw new ApiAcessoCidadaoException(Collections.singletonList(STATUS + response.statusCode()));
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage());
            throw new ApiAcessoCidadaoException(Collections.singletonList("Erro ao buscar agente publico por sub."));
        }
    }

    public AgentePublicoACDto buscarPessoaPorCpf(String cpf) {
        String sub = buscarSubPorCpf(cpf);
        return buscarAgentePublicoPorSub(sub);
    }

}
