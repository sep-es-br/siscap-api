package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.ACUserInfoDtoStringRole;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.enums.Permissoes;
import br.gov.es.siscap.exception.UsuarioSemPermissaoException;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
    private final ImagemPerfilService imagemPerfilService;
    private final PessoaService pessoaService;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final Roles roles;

    public UsuarioDto autenticar(String accessToken) {
        logger.info("Autenticar usuário SisCap.");
        ACUserInfoDto userInfo = getUserInfo(accessToken);
        Usuario usuario = buscarOuCriarUsuario(userInfo, accessToken);
        String token = tokenService.gerarToken(usuario);
        logger.info("Token JWT gerado.");
        byte[] imagemPerfil = new byte[0];
        try {
            Resource imagem = imagemPerfilService.buscar(usuario.getPessoa().getNomeImagem());
            imagemPerfil = imagem != null ? imagem.getContentAsByteArray() : null;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        Set<Permissoes> permissoes = null;
        if (usuario.getPapeis() != null && !usuario.getPapeis().isEmpty()) {
            logger.info("Buscando permissões de usuário");
            permissoes = usuario.getPapeis().stream().map(r -> roles.getRoles().get(r)).toList()
                    .stream().flatMap(List::stream).collect(Collectors.toSet());
            logger.info("Permissões de usuário encontradas com sucesso.");
        }

        return new UsuarioDto(token, usuario.getPessoa().getNome(), getEmailUserInfo(userInfo), usuario.getSub(),
                imagemPerfil, permissoes);
    }

    private Usuario buscarOuCriarUsuario(ACUserInfoDto userInfo, String accessToken) {
        Usuario usuario = (Usuario) usuarioRepository.findBySub(userInfo.subNovo());
        if (usuario != null) {
            logger.info("Usuário já existente, procedendo com atualizações de papeis e token.");
            usuario.setAccessToken(accessToken);
            usuario.setPapeis(userInfo.role());
            usuarioRepository.saveAndFlush(usuario);
            logger.info("Usuário atualizado com sucesso.");
            return usuario;
        }

        logger.info("Usuário inexistente, prosseguindo para criação de um novo usuário.");
        Pessoa pessoa;
        try {
            pessoa = pessoaService.buscarPorSub(userInfo.subNovo());
            logger.info("Foi encontrado uma pessoa com este sub, procedendo para criação de usuário para essa pessoa.");
        } catch (PessoaNaoEncontradoException e) {
            logger.info("Pessoa não encontrada, procedendo para criação de uma nova pessoa.");
            pessoa = new Pessoa();
            pessoa.setNome(userInfo.apelido());
            pessoa.setEmail(getEmailUserInfo(userInfo));
            pessoa.setSub(userInfo.subNovo());
            pessoa.setApagado(false);
            pessoa.setCriadoEm(LocalDateTime.now());
            pessoa = pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);
            logger.info("Pessoa criada com sucesso.");
        }

        usuario = new Usuario(null, userInfo.role(), pessoa, userInfo.subNovo(), accessToken);

        usuarioRepository.save(usuario);

        logger.info("Usuário criado com sucesso.");

        return usuario;
    }

    private static String getEmailUserInfo(ACUserInfoDto userInfo) {
        return userInfo.emailCorporativo() != null ? userInfo.emailCorporativo() : userInfo.email();
    }

    protected ACUserInfoDto getUserInfo(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://acessocidadao.es.gov.br/is/connect/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            ACUserInfoDto userInfoDto;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().contains("role\":\""))
                userInfoDto = new ACUserInfoDto(new ObjectMapper().readValue(response.body(), ACUserInfoDtoStringRole.class));
            else
                userInfoDto = new ObjectMapper().readValue(response.body(), ACUserInfoDto.class);

            if (userInfoDto.role() == null || userInfoDto.role().isEmpty())
                throw new UsuarioSemPermissaoException();

            return userInfoDto;
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        throw new ServiceSisCapException(List.of("Não foi possivel identificar um usuário no acesso cidadão com esse token. Faça login novamente!"));
    }
}
