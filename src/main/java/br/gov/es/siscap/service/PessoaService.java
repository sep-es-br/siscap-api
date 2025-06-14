package br.gov.es.siscap.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ResponsavelProponenteOpcoesDto;
import br.gov.es.siscap.exception.OrganizacaoSemResponsavelException;
import br.gov.es.siscap.exception.UsuarioSemAutorizacaoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaService {

	private final PessoaRepository repository;
	private final ImagemPerfilService imagemPerfilService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;
	private final UsuarioService usuarioService;
	private final AcessoCidadaoService acessoCidadaoService;
	private final ProjetoPessoaService projetoPessoaService;
	private final ProgramaPessoaService programaPessoaService;
	private final OrganogramaService organogramaService;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(PessoaService.class);

	@Value("${guidGOVES}")
	private String GUID_GOVES;

	public Page<PessoaListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todas as pessoas.");

		return repository.paginarPessoasPorFiltroPesquisaSimples(search, pageable)
					.map(pessoa -> {
						try {
							Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(pessoa);

							List<String> nomesOrganizacoes = this.mapearNomesOrganizacoesPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);

							return new PessoaListaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()), nomesOrganizacoes);
						} catch (IOException e) {
							throw new SiscapServiceException(Collections.singletonList(e.getMessage()));
						}
					});
	}

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(OpcoesDto::new).toList();
	}

	public PessoaDto buscarPorId(Long id) throws IOException {
		logger.info("Buscando pessoa com id: {}", id);

		Pessoa pessoa = buscar(id);

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(pessoa);
		Set<Long> idOrganizacoes = this.mapearIdOrganizacoesPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);
		Long idOrganizacaoResponsavel = this.mapearIdOrganizacaoResponsavelPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);

		return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()), idOrganizacoes, idOrganizacaoResponsavel);
	}

	@Transactional
	public PessoaDto cadastrar(PessoaForm form) throws IOException {
		logger.info("Cadastrando nova pessoa");
		logger.info("Dados: {}", form);

		validarPessoa(form);

		String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());

		Pessoa pessoa = repository.save(new Pessoa(form, nomeImagem));

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.cadastrarPorPessoa(pessoa, form.idOrganizacoes());
		Set<Long> idOrganizacoes = this.mapearIdOrganizacoesPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);
		Long idOrganizacaoResponsavel = this.mapearIdOrganizacaoResponsavelPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);

		logger.info("Pessoa cadastrada com sucesso");
		return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()), idOrganizacoes, idOrganizacaoResponsavel);
	}


	@Transactional
	public PessoaDto atualizar(Long id, PessoaForm form, Authentication auth) throws IOException {
		if (auth != null && !buscar(id).getSub().equals(((Usuario) auth.getPrincipal()).getSub())) {
			throw new UsuarioSemAutorizacaoException();
		}

		logger.info("Atualizando pessoa com id: {}", id);
		logger.info("Dados: {}", form);

		Pessoa pessoa = buscar(id);
		pessoa.atualizarPessoa(form);

		if (form.imagemPerfil() != null)
			pessoa.atualizarImagemPerfil(imagemPerfilService.atualizar(pessoa.getNomeImagem(), form.imagemPerfil()));
		else {
			imagemPerfilService.apagar(pessoa.getNomeImagem());
			pessoa.atualizarImagemPerfil(null);
		}

		Pessoa pessoaResultado = repository.save(pessoa);

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.atualizarPorPessoa(pessoaResultado, form.idOrganizacoes());
		Set<Long> idOrganizacoes = this.mapearIdOrganizacoesPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);
		Long idOrganizacaoResponsavel = this.mapearIdOrganizacaoResponsavelPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);

		logger.info("Pessoa atualizada com sucesso");
		return new PessoaDto(pessoaResultado, getImagemNotNull(pessoa.getNomeImagem()), idOrganizacoes, idOrganizacaoResponsavel);
	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo pessoa com id: {}", id);
		Pessoa pessoa = buscar(id);
		imagemPerfilService.apagar(pessoa.getNomeImagem());
		pessoa.apagarPessoa();

		usuarioService.excluirPorPessoa(pessoa.getId());

		repository.saveAndFlush(pessoa);
		repository.deleteById(id);

		pessoaOrganizacaoService.excluirPorPessoa(pessoa);
		projetoPessoaService.excluirPorPessoa(pessoa);
		programaPessoaService.excluirPorPessoa(pessoa);

		logger.info("Pessoa excluida com sucesso");
	}

	public OpcoesDto buscarResponsavelPorIdOrganizacao(Long orgId) {
		PessoaOrganizacao pessoaOrganizacao = pessoaOrganizacaoService.buscarPorOrganizacao(new Organizacao(orgId));

		if (pessoaOrganizacao != null) {
			logger.info("Responsavel encontrado, id: {}", pessoaOrganizacao.getPessoa().getId());
			return new OpcoesDto(pessoaOrganizacao.getPessoa());
		} else {
			throw new OrganizacaoSemResponsavelException();
		}
	}

	public boolean existePorId(Long id) {
		return repository.existsById(id);
	}

	public Pessoa buscarPorSub(String sub) {
		return repository.findBySub(sub).orElseThrow(() -> new PessoaNaoEncontradoException(sub));
	}

	public PessoaDto buscarMeuPerfil(String subNovo) throws IOException {
		Pessoa pessoa = buscarPorSub(subNovo);

		Resource imagem = imagemPerfilService.buscar(pessoa.getNomeImagem());
		byte[] conteudo = imagem != null ? imagem.getContentAsByteArray() : null;

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(pessoa);
		Set<Long> idOrganizacoes = this.mapearIdOrganizacoesPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);
		Long idOrganizacaoResponsavel = this.mapearIdOrganizacaoResponsavelPorPessoaOrganizacaoSet(pessoaOrganizacaoSet);

		return new PessoaDto(pessoa, conteudo, idOrganizacoes, idOrganizacaoResponsavel);
	}

	@Transactional
	public Pessoa salvarNovaPessoaAcessoCidadao(Pessoa pessoa) {
		return repository.save(pessoa);
	}

	public AgentePublicoACDto buscarPessoaNoAcessoCidadaoPorCpf(String cpf) {
		return acessoCidadaoService.buscarPessoaPorCpf(cpf);
	}

	@Transactional
	public void validarSub(String sub, Long idPessoa) {
		Pessoa pessoa = buscar(idPessoa);
		if (pessoa.getSub() == null) {
			pessoa.setSub(sub);
			repository.saveAndFlush(pessoa);
		} else if (!pessoa.getSub().equals(sub))
			throw new SiscapServiceException(Collections.singletonList("Falha na integridade do Sub do usuário"));
	}

	private Pessoa buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new PessoaNaoEncontradoException(id));
	}

	private byte[] getImagemNotNull(String nomeImagem) throws IOException {
		if (nomeImagem == null)
			return null;
		Resource imagemPerfil = imagemPerfilService.buscar(nomeImagem);
		byte[] conteudoImagem = null;
		if (imagemPerfil != null)
			conteudoImagem = imagemPerfil.getContentAsByteArray();
		return conteudoImagem;
	}

	private List<String> mapearNomesOrganizacoesPorPessoaOrganizacaoSet(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.map(PessoaOrganizacao::getOrganizacao)
					.map(Organizacao::getNome)
					.toList();
	}

	private Set<Long> mapearIdOrganizacoesPorPessoaOrganizacaoSet(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.map(pessoaOrganizacao -> pessoaOrganizacao.getOrganizacao().getId())
					.collect(Collectors.toSet());
	}

	private Long mapearIdOrganizacaoResponsavelPorPessoaOrganizacaoSet(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.filter(PessoaOrganizacao::getIsResponsavel)
					.findFirst()
					.map(pessoaOrganizacao -> pessoaOrganizacao.getOrganizacao().getId())
					.orElse(null);
	}

	private void validarPessoa(PessoaForm form) {
		List<String> erros = new ArrayList<>();

		boolean checkPessoaExistePorEmail = repository.existsByEmail(form.email());

		if (checkPessoaExistePorEmail)
			erros.add("Já existe uma pessoa cadastrada com esse email.");

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}
	}

    public List<ResponsavelProponenteOpcoesDto> listarOpcoesDropdownOrganizacao(String unidadeGuid) {
		
		List<ResponsavelProponenteOpcoesDto> listaResponsavelOrganizacao;
		
		String subGestorOrganizacao = acessoCidadaoService.buscarGestorPorGuidUnidade(unidadeGuid);
		
		listaResponsavelOrganizacao = acessoCidadaoService.buscarPessoasUnidadePapelPrioritario(unidadeGuid);

		List<ResponsavelProponenteOpcoesDto> listaAtualizada = listaResponsavelOrganizacao.stream()
																.map(p -> new ResponsavelProponenteOpcoesDto(
																p.id(),
																p.nome(),
																p.papelPrioritario(),
																p.agentePublicoSub(),
																subGestorOrganizacao.equals(p.agentePublicoSub()) // seta true se for o gestor
															))
															.collect(Collectors.toList());
		
		listaResponsavelOrganizacao = listaAtualizada ;

		return listaResponsavelOrganizacao;

    }

	public String buscarIdPorSub(String sub) {
		return repository.findBySub(sub)
			.map(p -> String.valueOf(p.getId()))
			.orElse("");
	}

	public List<ResponsavelProponenteOpcoesDto> listarOpcoesDropdownTodosAgentesGoves() {
		return acessoCidadaoService.buscarPessoasUnidadePapelPrioritario(GUID_GOVES);
	}

    public List<ResponsavelProponenteOpcoesDto> filtrarAgentesGovesPorTermo(String termo, CacheAgentesGovesService cacheService) {
		String termoLower = termo.toLowerCase();
		return cacheService.getCache().stream()
        .filter(agente -> 
            agente.nome().toLowerCase().contains(termoLower)
        )
        .collect(Collectors.toList());
    }

	public ResponsavelProponenteOpcoesDto buscarAgentesGovesPorSub(String sub, CacheAgentesGovesService cacheService) {
		
		try {
			Pessoa pessoaBanco = buscarPorSub(sub);
			return new ResponsavelProponenteOpcoesDto(
				pessoaBanco.getId(), 
				pessoaBanco.getNome(), 
				pessoaBanco.getNomeSocial(),
				sub,
				false );

		} catch (PessoaNaoEncontradoException e) {
			logger.debug("Agente não encontrado no banco, buscando no cache...");
		}
		
		return cacheService.getCache().stream()
        .filter(agente -> 
            agente.agentePublicoSub().contains(sub)
        )
        .findFirst()
		.orElse(null);

    }

	@Transactional
	public String sincronizarAgenteCidadaoPessoaSiscap( String sub ) {

		logger.info("Inicio sincronizar pessoa Acesso Cidadao com base do SISCAP.");

		AgentePublicoACDto dados = acessoCidadaoService.buscarPessoaPorSub(sub);

		Set<Organizacao> organizacoes = buscarOrganizacoesAssociadas(sub);
		
		Pessoa pessoa = construirPessoa(dados);
		
		pessoa = this.salvarNovaPessoaAcessoCidadao(pessoa);
		
		associarOrganizacoesAPessoa(pessoa, organizacoes);

		logger.info("Pessoa criada com sucesso.");

		return pessoa.getId().toString();

	}
	
	private Pessoa construirPessoa(AgentePublicoACDto dados) {
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(dados.nome().toUpperCase());
		pessoa.setNomeSocial(dados.apelido());
		pessoa.setEmail(dados.email());
		pessoa.setSub(dados.sub());
		pessoa.setApagado(false);
		pessoa.setCriadoEm(LocalDateTime.now());
		return pessoa;
	}

	private Set<Organizacao> buscarOrganizacoesAssociadas(String sub) {
		Set<Organizacao> organizacoes = new HashSet<>();
		String lotacaoGuidPrioritaria = buscarLotacaoGuidPrioritaria(sub);
		if (!lotacaoGuidPrioritaria.isEmpty()) {
			buscarOrganizacaoPorLotacao(lotacaoGuidPrioritaria)
				.ifPresentOrElse(
					organizacoes::add,
					() -> logger.info("Organização não encontrada para o CNPJ fornecido.")
				);
		}
		return organizacoes;
	}

	private void associarOrganizacoesAPessoa(Pessoa pessoa, Set<Organizacao> organizacoes) {
    	Set<Long> idsOrganizacoes = organizacoes.stream()
            .map(Organizacao::getId)
            .collect(Collectors.toSet());
    	pessoaOrganizacaoService.cadastrarPorPessoa(pessoa, idsOrganizacoes);
	}

	private String buscarLotacaoGuidPrioritaria(String sub) {
		return acessoCidadaoService.listarPapeisAgentePublicoPorSub(sub)
				.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::LotacaoGuid)
				.orElse("");
	}

	private Optional<Organizacao> buscarOrganizacaoPorLotacao(String lotacaoGuid) {
    	String guidOrganizacao = organogramaService.listarUnidadeInfoPorLotacaoGuid(lotacaoGuid).guidOrganizacao();
    	String cnpjOrganizacao = organogramaService.listarDadosOrganizacaoPorGuid(guidOrganizacao).cnpj();
    	return organizacaoService.buscarPorCnpj(cnpjOrganizacao);
	}


}