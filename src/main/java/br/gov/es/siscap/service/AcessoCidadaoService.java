package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.models.Pessoa;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class AcessoCidadaoService {

    private final Logger logger = LogManager.getLogger(AcessoCidadaoService.class);
    private final ImagemPerfilService imagemPerfilService;
    private final PessoaService pessoaService;

    public UsuarioDto montarUsuarioDto(String accessToken) {
        UsuarioDto user = null;
        try {
            ACUserInfoDto userInfoDto = obterUserInfo(accessToken);
            user = new UsuarioDto(userInfoDto.apelido(), userInfoDto.email(), null);
            Pessoa pessoa = pessoaService.buscarPorEmail(userInfoDto.email());
            Resource imagemPerfil = imagemPerfilService.buscar(pessoa.getNomeImagem());
            user = new UsuarioDto(userInfoDto.apelido(), userInfoDto.email(), imagemPerfil.getContentAsByteArray());
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        } catch (PessoaNaoEncontradoException ex) {
            logger.warn(ex.getMessage());
        }

        return user;
    }

    public ACUserInfoDto obterUserInfo(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://acessocidadao.es.gov.br/is/connect/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(response.body(), ACUserInfoDto.class);
    }
}
