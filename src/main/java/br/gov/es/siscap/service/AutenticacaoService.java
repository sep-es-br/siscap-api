package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.ACUserInfoDtoStringRole;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.enums.Permissoes;
import br.gov.es.siscap.exception.UsuarioSemPermissaoException;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
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
	private final AcessoCidadaoService acessoCidadaoService;
	private final ImagemPerfilService imagemPerfilService;
	private final PessoaService pessoaService;
	private final TokenService tokenService;
	private final UsuarioRepository usuarioRepository;
	private final Roles roles;

	public UsuarioDto autenticar(String accessToken) {
		logger.info("Autenticar usuário SisCap.");

		ACUserInfoDto userInfo = acessoCidadaoService.buscarInformacoesUsuario(accessToken);

		Usuario usuario = buscarOuCriarUsuario(userInfo, accessToken);
		String token = tokenService.gerarToken(usuario);
		logger.info("Token JWT gerado.");

		byte[] imagemPerfil = construirImagemPerfilUsuario(usuario.getPessoa().getNomeImagem());

		Set<Permissoes> permissoes = construirPermissoesSet(usuario.getPapeis());

		return new UsuarioDto(token, usuario.getPessoa().getNome(), getEmailUserInfo(userInfo), usuario.getSub(),
					imagemPerfil, permissoes);
	}

	private Usuario buscarOuCriarUsuario(ACUserInfoDto userInfo, String accessToken) {
		Usuario usuario = (Usuario) usuarioRepository.findBySub(userInfo.subNovo());
		if (usuario != null) {
			pessoaService.validarSub(userInfo.subNovo(), usuario.getPessoa().getId());
			logger.info("Usuário já existente, procedendo com atualizações de papeis e token.");
			usuario.setAccessToken(accessToken);
			usuario.setPapeis(userInfo.role());
			usuarioRepository.saveAndFlush(usuario);
			logger.info("Usuário atualizado com sucesso.");
			return usuario;
		}

		/*
			SOLUCAO TA AQUI

			SE userInfo.role() == null OU userInfo.role().isEmpty() ->
			|-> ADICIONAR PERMISSAO DE "PROPONENTE" (MAIS BASICAO, SO PROJETO_CADASTRAR E  PROJETO_EDITAR)

			OBS: VERIFICAR SE userInfo TRAZ agentePublico == true PRA ATRIBUIR PERMISSAO DE "PROPONENTE"
			|-> EX: CIDADAO COMUM (NAO AGENTEPUBLICO) -> throw new UsuarioSemPermissaoException

			!!! PRECISA DE USUARIO NOVO
										|-> SEM TER ENTRADA NO BANCO OU PERMISSAO NO PAINEL
										DE CONTROLE ADMINISTRATIVO DO ACESSO CIDADAO
					E QUE SEJA AGENTE PUBLICO PRA TESTAR !!!
		*/

		/*

			FLUXO PAPEIS/ROLES:

			1. CHECA SE userInfo.role() == null OU userInfo.role().isEmpty()
				 |-> SE NAO, SEGUE O JOGO NORMAL
				 |-> SE SIM, PASSO 2

			2. CHECA SE userInfo.agentePublico() == true
				 |-> SE NAO, throw new UsuarioSemPermissaoException
				 |-> SE SIM, PASSO 3

			3. BUSCA "LotacaoGuid" DO PAPEL DO AGENTE PUBLICO
				 |-> SE LOTACAO ('UNIDADE' DO ORGANOGRAMA) FOR 'SUBCAP' -> DAR PAPEL 'SUBCAP'
				 |-> SE NAO, DAR PAPEL 'PROPONENTE'

		*/

		logger.info("Usuário inexistente, prosseguindo para criação de um novo usuário.");
		Pessoa pessoa;
		try {
			pessoa = pessoaService.buscarPorSub(userInfo.subNovo());
			logger.info("Foi encontrado uma pessoa com este sub, procedendo para criação de usuário para essa pessoa.");
		} catch (PessoaNaoEncontradoException e) {
			pessoa = criarPessoa(userInfo);
		}

		usuario = new Usuario(null, userInfo.role(), pessoa, userInfo.subNovo(), accessToken);

		usuarioRepository.save(usuario);

		logger.info("Usuário criado com sucesso.");

		return usuario;
	}

	/*
		USAR API DO ORGANOGRAMA PRA PROCURAR AS ORGANIZACOES
		DA QUAL O AGENTE PUBLICO PERTENCE
		E ENTAO VINCULAR PESSOAORGANIZACAO NO MOMENTO DA CRIACAO
		|-> IDEIA EH FACILITAR O PRIMEIRO ACESSO DO PROPONENTE (PESSOA DE OUTRO ORGAO)
				E AUXILIAR NO PREENCHIMENTO DE "INTENCAO/PROPOSTA DE PROJETO"
	*/
	private Pessoa criarPessoa(ACUserInfoDto userInfo) {
		Pessoa pessoa;
		logger.info("Pessoa não encontrada, procedendo para criação de uma nova pessoa.");
		pessoa = new Pessoa();
		pessoa.setNome(userInfo.apelido());
		pessoa.setEmail(getEmailUserInfo(userInfo));
		pessoa.setSub(userInfo.subNovo());
		pessoa.setApagado(false);
		pessoa.setCriadoEm(LocalDateTime.now());
		pessoa = pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);
		logger.info("Pessoa criada com sucesso.");
		return pessoa;
	}

	private byte[] construirImagemPerfilUsuario(String nomeImagem) {
		byte[] imagemPerfil = new byte[0];

		try {
			Resource imagem = imagemPerfilService.buscar(nomeImagem);
			imagemPerfil = imagem != null ? imagem.getContentAsByteArray() : null;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return imagemPerfil;
	}

	private Set<Permissoes> construirPermissoesSet(Set<String> usuarioPapeis) {
		Set<Permissoes> permissoes = null;
		if (usuarioPapeis != null && !usuarioPapeis.isEmpty()) {
			logger.info("Buscando permissões de usuário");
			permissoes = usuarioPapeis.stream().map(r -> roles.getRoles().get(r)).toList()
						.stream().flatMap(List::stream).collect(Collectors.toSet());
			logger.info("Permissões de usuário encontradas com sucesso.");
		}

		return permissoes;
	}

	private static String getEmailUserInfo(ACUserInfoDto userInfo) {
		return userInfo.emailCorporativo() != null ? userInfo.emailCorporativo() : userInfo.email();
	}
}
