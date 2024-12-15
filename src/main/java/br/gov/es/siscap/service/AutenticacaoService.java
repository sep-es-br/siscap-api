package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.dto.ACUserInfoDtoStringRole;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.enums.Permissoes;
import br.gov.es.siscap.exception.UsuarioSemAutorizacaoException;
import br.gov.es.siscap.exception.UsuarioSemPermissaoException;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

	private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
	private final AcessoCidadaoService acessoCidadaoService;
	private final OrganogramaService organogramaService;
	private final ImagemPerfilService imagemPerfilService;
	private final PessoaService pessoaService;
	private final OrganizacaoService organizacaoService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;
	private final TokenService tokenService;
	private final UsuarioRepository usuarioRepository;
	private final Roles roles;

	private final String LOTACAOGUID_SUBCAP = "515685f7-2aeb-4cc4-a311-8b793297f8fb";

	public UsuarioDto autenticar(String accessToken) {
		logger.info("Autenticar usuário SisCap.");

		ACUserInfoDto userInfo = acessoCidadaoService.buscarInformacoesUsuario(accessToken);

		if (!userInfo.agentepublico() && (userInfo.role() == null || userInfo.role().isEmpty()))
			throw new UsuarioSemAutorizacaoException();

		Usuario usuario = buscarOuCriarUsuario(userInfo, accessToken);
		String token = tokenService.gerarToken(usuario);
		logger.info("Token JWT gerado.");

		byte[] imagemPerfil = construirImagemPerfilUsuario(usuario.getPessoa().getNomeImagem());

		Set<Permissoes> permissoes = construirPermissoesSet(usuario.getPapeis());

		Set<Long> idOrganizacoes = construirIdOrganizacoesSet(usuario.getPessoa(), usuario.getSub());

		boolean isProponente = usuario.getPapeis().size() == 1 && usuario.getPapeis().contains("PROPONENTE");

		return new UsuarioDto(token, usuario.getPessoa().getNome(), getEmailUserInfo(userInfo), usuario.getSub(),
					imagemPerfil, permissoes, idOrganizacoes, isProponente);
	}

	private Usuario buscarOuCriarUsuario(ACUserInfoDto userInfo, String accessToken) {
		Usuario usuario = (Usuario) usuarioRepository.findBySub(userInfo.subNovo());
		if (usuario != null) {
			pessoaService.validarSub(userInfo.subNovo(), usuario.getPessoa().getId());
			logger.info("Usuário já existente, procedendo com atualizações de papeis e token.");
			usuario.setAccessToken(accessToken);
			usuario.setPapeis(validarPapeisUsuario(userInfo));
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

		usuario = new Usuario(null, validarPapeisUsuario(userInfo), pessoa, userInfo.subNovo(), accessToken);

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

	private Set<String> validarPapeisUsuario(ACUserInfoDto userInfo) {

		String sub = userInfo.subNovo();
		Set<String> usuarioPapeis = userInfo.role();

		if (usuarioPapeis != null && !usuarioPapeis.isEmpty()) return usuarioPapeis;

		Set<String> papeisLotacaoGuidSet = listarPapeisLotacaoGuid(sub);

		if (papeisLotacaoGuidSet.contains(LOTACAOGUID_SUBCAP)) {
			usuarioPapeis.add("SUBCAP");
		} else {
			usuarioPapeis.add("PROPONENTE");
		}

		return usuarioPapeis;
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

	private Set<Long> construirIdOrganizacoesSet(Pessoa usuarioPessoa, String subNovo) {
		logger.info("Buscando organizações do usuário.");

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(usuarioPessoa);

		if (pessoaOrganizacaoSet != null && !pessoaOrganizacaoSet.isEmpty()) {
			logger.info("Organizações do usuário encontradas com sucesso.");
			return pessoaOrganizacaoSet.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).collect(Collectors.toSet());
		} else {
			logger.info("Usuário não está vinculado a nenhuma organizacao.");
			logger.info("Iniciando processo de vinculação de usuário a organizações.");
			Set<PessoaOrganizacao> pessoaOrganizacaoSetNovo = vincularPessoaOrganizacoes(usuarioPessoa, subNovo);
			logger.info("Vínculo entre pessoa e organizações realizado com sucesso.");
			return pessoaOrganizacaoSetNovo.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).collect(Collectors.toSet());
		}
	}

	private Set<PessoaOrganizacao> vincularPessoaOrganizacoes(Pessoa pessoa, String subNovo) {
		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();
		Set<String> papeisLotacaoGuidSet = listarPapeisLotacaoGuid(subNovo);
		Set<Organizacao> organizacoes = new HashSet<>();

		for (String lotacaoGuid : papeisLotacaoGuidSet) {
			String guidOrganizacao = organogramaService.listarUnidadeInfoPorLotacaoGuid(lotacaoGuid).guidOrganizacao();
			String cnpjOrganizacao = organogramaService.listarDadosOrganizacaoPorGuid(guidOrganizacao).cnpj();

			try {
				Organizacao organizacao = organizacaoService.buscarPorCnpj(cnpjOrganizacao);
				organizacoes.add(organizacao);
			} catch (Exception e) {
				logger.info("Organização não encontrada para o CNPJ fornecido.");
			}
		}

		for (Organizacao organizacao : organizacoes) {
			PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(pessoa, organizacao);
			pessoaOrganizacaoSet.add(pessoaOrganizacao);
		}

		return pessoaOrganizacaoService.salvarPessoaOrganizacaoSetAutenticacaoUsuario(pessoaOrganizacaoSet);
	}

	private Set<String> listarPapeisLotacaoGuid(String subNovo) {
		return acessoCidadaoService.listarPapeisAgentePublicoPorSub(subNovo).stream()
					.map(ACAgentePublicoPapelDto::LotacaoGuid)
					.map(String::toLowerCase)
					.collect(Collectors.toSet());
	}

	private static String getEmailUserInfo(ACUserInfoDto userInfo) {
		return userInfo.emailCorporativo() != null ? userInfo.emailCorporativo() : userInfo.email();
	}
}
