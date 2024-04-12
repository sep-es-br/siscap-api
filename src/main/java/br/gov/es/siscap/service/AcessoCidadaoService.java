package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.ACUserInfoDtoStringRole;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcessoCidadaoService {

    private final Logger logger = LogManager.getLogger(AcessoCidadaoService.class);
    private final ImagemPerfilService imagemPerfilService;
    private final PessoaService pessoaService;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final Roles roles;

    @Transactional
    public UsuarioDto autenticar(String accessToken) {
        ACUserInfoDto userInfo = getUserInfo(accessToken);
        Usuario usuario = buscarOuCriarUsuario(userInfo, accessToken);
        String token = tokenService.gerarToken(usuario);
        byte[] imagemPerfil = new byte[0];
        try {
            Resource imagem = imagemPerfilService.buscar(usuario.getPessoa().getNomeImagem());
            imagemPerfil = imagem != null ? imagem.getContentAsByteArray() : null;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new UsuarioDto(token, usuario.getPessoa().getNome(), usuario.getEmail(), usuario.getSubNovo(),
                imagemPerfil, usuario.getPapeis().stream().map(r -> roles.getRoles().get(r)).toList()
                .stream().flatMap(List::stream).collect(Collectors.toSet()));
    }

    private Usuario buscarOuCriarUsuario(ACUserInfoDto userInfo, String accessToken) {
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(userInfo.email());
        if (usuario != null) {
            usuario.setAccessToken(accessToken);
            usuario.setPapeis(userInfo.role());
            usuarioRepository.saveAndFlush(usuario);
            return usuario;
        }

        Pessoa pessoa;
        try {
            pessoa = pessoaService.buscarPorEmail(userInfo.email());
        } catch (PessoaNaoEncontradoException e) {
            pessoa = new Pessoa();
            pessoa.setNome(userInfo.apelido());
            pessoa.setEmail(userInfo.email());
            pessoa.setApagado(false);
        }
        pessoa = pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);

        usuario = new Usuario(userInfo.email(), null, userInfo.role(), pessoa, userInfo.subNovo(), accessToken);

        usuarioRepository.save(usuario);

        return usuario;
    }

    protected ACUserInfoDto getUserInfo(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://acessocidadao.es.gov.br/is/connect/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().contains("role\":\"")) {
                ACUserInfoDtoStringRole acUserInfoDtoStringRole = new ObjectMapper().readValue(response.body(), ACUserInfoDtoStringRole.class);
                return new ACUserInfoDto(acUserInfoDtoStringRole);
            }

            return new ObjectMapper().readValue(response.body(), ACUserInfoDto.class);
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        throw new ServiceSisCapException(List.of("Não foi possivel identificar um usuário no acesso cidadão com esse token. Faça login novamente!"));
    }
}
