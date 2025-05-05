package br.gov.es.siscap.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACUserInfoDto;
import br.gov.es.siscap.enums.Permissoes;
import br.gov.es.siscap.exception.UsuarioSemAutorizacaoException;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

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
	
	public UsuarioDto autenticar(String accessToken) {
		logger.info("Autenticar usuário SisCap.");

		ACUserInfoDto userInfo = acessoCidadaoService.buscarInformacoesUsuario(accessToken);

		logger.info("Informações do usuario : {}", userInfo );
						
		if ( Boolean.FALSE.equals(userInfo.agentepublico() ) && ( userInfo.role() == null || userInfo.role().isEmpty() ) )
			throw new UsuarioSemAutorizacaoException();

		Set<ACAgentePublicoPapelDto> papeisSet = listarPapeis(userInfo.subNovo());

		boolean isProponente = false;

		if ( userInfo.role() == null || userInfo.role().isEmpty() ) 
			isProponente = true ;
		
		if ( Boolean.FALSE.equals(isProponente) && ( userInfo.role() == null || userInfo.role().isEmpty() ) )
			throw new UsuarioSemAutorizacaoException();

		if ( isProponente )
			userInfo.role().add("PROPONENTE");

		logger.info("Perfis do usuario : {}", userInfo.role() );

		Usuario usuario = buscarOuCriarUsuario(userInfo, accessToken);
		String token = tokenService.gerarToken(usuario);
		logger.info("Token JWT gerado.");

		byte[] imagemPerfil = construirImagemPerfilUsuario(usuario.getPessoa().getNomeImagem());
		
		Set<Permissoes> permissoes = construirPermissoesSet(usuario.getPapeis());

		Set<Long> idOrganizacoes = construirIdOrganizacoesSet(usuario.getPessoa(), usuario.getSub());
				
		return new UsuarioDto(token, usuario.getPessoa().getNome(), getEmailUserInfo(userInfo), usuario.getSub(),
			imagemPerfil, permissoes, idOrganizacoes, usuario.getPessoa().getId(), isProponente );

	}

	private Usuario buscarOuCriarUsuario(ACUserInfoDto userInfo, String accessToken) {
		Usuario usuario = (Usuario) usuarioRepository.findBySub(userInfo.subNovo());
		if (usuario != null) {
			pessoaService.validarSub(userInfo.subNovo(), usuario.getPessoa().getId());

			atualizarNomeNomeSocialPessoa(usuario.getPessoa(), userInfo);

			logger.info("Usuário já existente, procedendo com atualizações de papeis e token.");
			usuario.setAccessToken(accessToken);
			usuario.setPapeis(validarPapeisUsuario(userInfo));
			usuarioRepository.saveAndFlush(usuario);

			logger.info("Usuário atualizado com sucesso.");
			return usuario;
		}

		logger.info("Usuário inexistente, prosseguindo para criação de um novo usuário.");
		Pessoa pessoa;
		try {
			pessoa = pessoaService.buscarPorSub(userInfo.subNovo());
			logger.info("Foi encontrado uma pessoa com este sub, procedendo para criação de usuário para essa pessoa.");
			pessoa = atualizarNomeNomeSocialPessoa(pessoa, userInfo);
		} catch (PessoaNaoEncontradoException e) {
			pessoa = criarPessoa(userInfo);
		}

		usuario = new Usuario(null, validarPapeisUsuario(userInfo), pessoa, userInfo.subNovo(), accessToken);

		usuarioRepository.save(usuario);

		logger.info("Usuário criado com sucesso.");

		return usuario;
	}

	private Pessoa criarPessoa(ACUserInfoDto userInfo) {
		Pessoa pessoa;
		logger.info("Pessoa não encontrada, procedendo para criação de uma nova pessoa.");
		pessoa = new Pessoa();
		pessoa.setNome(userInfo.nome());
		pessoa.setNomeSocial(userInfo.apelido());
		pessoa.setEmail(getEmailUserInfo(userInfo));
		pessoa.setSub(userInfo.subNovo());
		pessoa.setApagado(false);
		pessoa.setCriadoEm(LocalDateTime.now());
		pessoa = pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);
		logger.info("Pessoa criada com sucesso.");
		return pessoa;
	}

	private Pessoa atualizarNomeNomeSocialPessoa(Pessoa pessoa, ACUserInfoDto userInfo) {
		logger.info("Atualizando nome e nomeSocial da pessoa vinculada ao usuário.");
		pessoa.setNome(userInfo.nome());
		pessoa.setNomeSocial(userInfo.apelido());
		return pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);
	}

	private Set<String> validarPapeisUsuario(ACUserInfoDto userInfo) {
		Set<String> usuarioPapeis = userInfo.role();
		if (usuarioPapeis != null && !usuarioPapeis.isEmpty()) return usuarioPapeis;
		return new HashSet<>();
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

		// busca todos as organizações da pessoa no banco
		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(usuarioPessoa);

		if (pessoaOrganizacaoSet != null && !pessoaOrganizacaoSet.isEmpty()) {
			logger.info("Organizações do usuário encontradas com sucesso.");

			// busca as organizações segundo acesso cidadão que estão no banco
			Set<Organizacao> organizacoesAc = getOrganizacoesDaPessoaAC(usuarioPessoa, subNovo);

			// busca as organizações segundo o banco atual
			Set<PessoaOrganizacao> organizacoesBanco = pessoaOrganizacaoService.buscarPorPessoa(usuarioPessoa);

			// desvincular as organizacoes que estão sobrando no banco
			Set<PessoaOrganizacao> organizacoesSobrando = organizacoesBanco.stream()
															.filter(o -> {
																return o.getOrganizacao().getGuid() != null &&
																		!o.getOrganizacao().getGuid().isBlank() &&
																		!organizacoesAc.stream()
																		.map(Organizacao::getGuid)
																		.toList().contains(o.getOrganizacao().getGuid());
															}).collect(Collectors.toSet());
			
			pessoaOrganizacaoService.excluirTodosPorId(organizacoesSobrando.stream().map(PessoaOrganizacao::getId).toList());
			
            final Set<PessoaOrganizacao> organizacoesBancoFinal = organizacoesBanco;

			// vincular as organizações que estão faltando
			Set<Organizacao> organizacoesFaltando = organizacoesAc.stream()
														.filter(organizacao -> {
															return !organizacoesBancoFinal.stream()
																.map(PessoaOrganizacao::getOrganizacao)
																.map(Organizacao::getGuid)
																.toList().contains(organizacao.getGuid());
														}).collect(Collectors.toSet());
		
														
			HashSet<PessoaOrganizacao> pessoaOrganizacaosFaltando = new HashSet<>();	
			for (Organizacao organizacao : organizacoesFaltando) {
				PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(usuarioPessoa, organizacao);
				pessoaOrganizacaosFaltando.add(pessoaOrganizacao);
			}


			pessoaOrganizacaosFaltando = new HashSet<>(pessoaOrganizacaoService.salvarPessoaOrganizacaoSetAutenticacaoUsuario(pessoaOrganizacaosFaltando));

			// vinculo do banco menos os que foram removidos
			
			organizacoesBanco = new HashSet<>(organizacoesBanco.stream()
								.filter(oBanco -> !organizacoesSobrando.stream().map(PessoaOrganizacao::getId).toList().contains(oBanco.getId()))
								.toList());
			
			// mais o que estavam faltando
			organizacoesBanco.addAll(pessoaOrganizacaosFaltando);

			return organizacoesBanco.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).collect(Collectors.toSet());
		} else {
			logger.info("Usuário não está vinculado a nenhuma organizacao.");
			logger.info("Iniciando processo de vinculação de usuário a organizações.");
			Set<PessoaOrganizacao> pessoaOrganizacaoSetNovo = vincularPessoaOrganizacoes(usuarioPessoa, subNovo);
			logger.info("Vínculo entre pessoa e organizações realizado com sucesso.");
			return pessoaOrganizacaoSetNovo.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).collect(Collectors.toSet());
		}


		
	}

	private Set<Organizacao> getOrganizacoesDaPessoaAC(Pessoa pessoa, String subNovo){
		Set<Organizacao> organizacoesSet = new HashSet<>();

		Set<ACAgentePublicoPapelDto> papeisSet = listarPapeis(subNovo);

		if(papeisSet.isEmpty()) {
			logger.info("Usuario [sub: {1}] não possui papel", subNovo);
			return organizacoesSet;
		}

		ACAgentePublicoPapelDto papel;

		if (papeisSet.size() == 1){
			papel = papeisSet.iterator().next();
			if(papel.LotacaoGuid().isBlank()) {
				logger.info("O papel do usuário [{1}] não possui GUID de Lotação.", subNovo);
				return organizacoesSet;
			} 
		} else {
			papeisSet = papeisSet.stream().filter(p -> p.Prioritario()).collect(Collectors.toSet());
			if(papeisSet.isEmpty()) {
				return new HashSet<>();
			} else {
				papel = papeisSet.iterator().next();
			}
		}

		String guidOrganizacao = organogramaService.listarUnidadeInfoPorLotacaoGuid(papel.LotacaoGuid()).guidOrganizacao();
		String cnpjOrganizacao = organogramaService.listarDadosOrganizacaoPorGuid(guidOrganizacao).cnpj();

		/*
			30/12/2024

			PROBLEMA:
				METODO organizacaoService.buscarPorCnpj TRAZIA ENTIDADE Organizacao
				|-> NAO CONTEMPLAVA CASO DE NAO ENCONTRAR ORGANIZACAO COM O CNPJ FORNECIDO (TRAZIA Organizacao = null)

			ABORDAGEM:
				METODO AGORA TRAZ Optional<Organizacao> E TRATA CASO DE ORGANIZACAO AUSENTE
				DENTRO DESTE METODO
				|-> SEM throw new RunTimeException PARA EVITAR DE IMPEDIR ACESSO DO USUARIO

			OBSERVACAO:
				ABORDAGEM CORRETA SERIA PREENCHER O CNPJ DAS ORGANIZACOES APROPRIADAMENTE
				DE ACORDO COM O RETORNO DA API DO ORGANOGRAMA
				|-> LEVANTA QUESTAO SINCRONIA DO BANCO DO SISCAP COM A API:
						* MELHOR SERIA A ABORDAGEM DO VAGNER DE TRAZER OS DADOS
							DA(S) ORGANIZACAO(OES) DIRETO DE UMA REQUISICAO
							PRA API DO ORGANOGRAMA
							|-> POREM SEM TEMPO
		*/

		Optional<Organizacao> organizacaoOptional = organizacaoService.buscarPorCnpj(cnpjOrganizacao);

		if (organizacaoOptional.isPresent()) {
			organizacoesSet.add(organizacaoOptional.get());
		} else {
			logger.info("Organização não encontrada no banco para o CNPJ fornecido: [{}].", cnpjOrganizacao);
		}

		return organizacoesSet;

	}

	private Set<PessoaOrganizacao> vincularPessoaOrganizacoes(Pessoa pessoa, String subNovo) {
		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();
		Set<Organizacao> organizacoesSet = new HashSet<>();

		Set<String> papeisLotacaoGuidSet = listarPapeisLotacaoGuid(subNovo);

		if (papeisLotacaoGuidSet.size() == 1 && papeisLotacaoGuidSet.iterator().next().isBlank()) {
			logger.info("Papeis do usuário não possuem GUID de Lotação.");
			return pessoaOrganizacaoSet;
		}

		for (String lotacaoGuid : papeisLotacaoGuidSet) {
			String guidOrganizacao = organogramaService.listarUnidadeInfoPorLotacaoGuid(lotacaoGuid).guidOrganizacao();
			String cnpjOrganizacao = organogramaService.listarDadosOrganizacaoPorGuid(guidOrganizacao).cnpj();

			/*
				30/12/2024

				PROBLEMA:
					METODO organizacaoService.buscarPorCnpj TRAZIA ENTIDADE Organizacao
					|-> NAO CONTEMPLAVA CASO DE NAO ENCONTRAR ORGANIZACAO COM O CNPJ FORNECIDO (TRAZIA Organizacao = null)

				ABORDAGEM:
					METODO AGORA TRAZ Optional<Organizacao> E TRATA CASO DE ORGANIZACAO AUSENTE
					DENTRO DESTE METODO
					|-> SEM throw new RunTimeException PARA EVITAR DE IMPEDIR ACESSO DO USUARIO

				OBSERVACAO:
					ABORDAGEM CORRETA SERIA PREENCHER O CNPJ DAS ORGANIZACOES APROPRIADAMENTE
					DE ACORDO COM O RETORNO DA API DO ORGANOGRAMA
					|-> LEVANTA QUESTAO SINCRONIA DO BANCO DO SISCAP COM A API:
							* MELHOR SERIA A ABORDAGEM DO VAGNER DE TRAZER OS DADOS
							  DA(S) ORGANIZACAO(OES) DIRETO DE UMA REQUISICAO
							  PRA API DO ORGANOGRAMA
								|-> POREM SEM TEMPO
			*/

			Optional<Organizacao> organizacaoOptional = organizacaoService.buscarPorCnpj(cnpjOrganizacao);

			if (organizacaoOptional.isPresent()) {
				organizacoesSet.add(organizacaoOptional.get());
			} else {
				logger.info("Organização não encontrada para o CNPJ fornecido: [{}].", cnpjOrganizacao);
			}
		}

		for (Organizacao organizacao : organizacoesSet) {
			PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(pessoa, organizacao);
			pessoaOrganizacaoSet.add(pessoaOrganizacao);
		}


		return pessoaOrganizacaoSet.isEmpty() ? pessoaOrganizacaoSet : pessoaOrganizacaoService.salvarPessoaOrganizacaoSetAutenticacaoUsuario(pessoaOrganizacaoSet);
	}

	private Set<String> listarPapeisLotacaoGuid(String subNovo) {
		return acessoCidadaoService.listarPapeisAgentePublicoPorSub(subNovo).stream()
					.map(papel -> papel.LotacaoGuid() != null ? papel.LotacaoGuid() : "")
					.map(String::toLowerCase)
					.collect(Collectors.toSet());
	}

	private Set<ACAgentePublicoPapelDto> listarPapeis(String subNovo) {
		return new HashSet<ACAgentePublicoPapelDto>(acessoCidadaoService.listarPapeisAgentePublicoPorSub(subNovo));
	}

	private static String getEmailUserInfo(ACUserInfoDto userInfo) {
		return userInfo.emailCorporativo() != null ? userInfo.emailCorporativo() : userInfo.email();
	}
}