package br.gov.es.siscap.service;

import br.gov.es.siscap.client.AcessoCidadaoTokenClient;
import br.gov.es.siscap.client.AcessoCidadaoWebClient;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.acessocidadaoapi.LoginACResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcessoCidadaoService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Value("${api.acessocidadao.client-id}")
    private String clientId;
    @Value("${api.acessocidadao.client-secret}")
    private String clientSecret;
    @Value("${api.acessocidadao.grant_type}")
    private String grantType;
    @Value("${api.acessocidadao.scope}")
    private String scope;

    private final AcessoCidadaoTokenClient tokenClient;
    private final AcessoCidadaoWebClient webClient;

    public AgentePublicoACDto buscarPessoaPorCpf(String cpf) {
        String sub = buscarSubPorCpf(cpf);
        return buscarAgentePublicoPorSub(sub);
    }

    private HashMap<String, Object> obterAuthorizationHeader() {
        final String basicToken = clientId + ":" + clientSecret;
        Map<String, Object> headers = getTokenClientHeaders(basicToken);
        String form = getTokenClientForm();
        LoginACResponseDto loginACResponseDto = tokenClient.login(headers, form);
        HashMap<String, Object> authorizationHeader = new HashMap<>();
        authorizationHeader.put(AUTHORIZATION, BEARER + loginACResponseDto.accessToken());
        return authorizationHeader;
    }

    private String getTokenClientForm() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", grantType);
        parameters.put("scope", scope);

        return parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    private Map<String, Object> getTokenClientHeaders(String basicToken) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(AUTHORIZATION, "Basic " + Base64.getEncoder()
                .encodeToString(basicToken.getBytes(StandardCharsets.UTF_8)));
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    private String buscarSubPorCpf(String cpf) {
        return webClient.buscarSubPorCpf(obterAuthorizationHeader(), cpf).sub();
    }

    private AgentePublicoACDto buscarAgentePublicoPorSub(String sub) {
        return new AgentePublicoACDto(webClient.buscarAgentePublicoPorSub(obterAuthorizationHeader(), sub));
    }

}
