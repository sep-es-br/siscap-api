package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.*;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ProjetoPropostoOpcoesDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.exception.RelatorioNomeArquivoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProjetoRepository;
import br.gov.es.siscap.specification.ProjetoSpecification;
import br.gov.es.siscap.utils.FormatadorCountAno;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.gov.es.siscap.models.ProjetoIndicador;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

	private final ProjetoRepository repository;
	private final ProjetoPessoaService projetoPessoaService;
	private final LocalidadeQuantiaService localidadeQuantiaService;
	private final OrganizacaoService organizacaoService;
	private final PessoaService pessoaService;
	private final AcessoCidadaoService acessoCidadaoService;
	private final ProjetoIndicadorService projetoIndicadorService;

	private final Logger logger = LogManager.getLogger(ProjetoService.class);

	public Page<ProjetoListaDto> listarTodos(
				Pageable pageable,
				String siglaOuTitulo,
				Long idOrganizacao,
				String status) {

		Specification<Projeto> especificacaoSiglaTitulo = siglaOuTitulo.isBlank() ? null : ProjetoSpecification.filtroSiglaTitulo(siglaOuTitulo);
		Specification<Projeto> especificacaoIdOrganizacao = idOrganizacao == 0 ? null : ProjetoSpecification.filtroIdOrganizacao(idOrganizacao);
		Specification<Projeto> especificacaoStatus = status.equals("Status") ? null : ProjetoSpecification.filtroStatus(status);

		Specification<Projeto> filtroPesquisa = Specification
					.where(especificacaoSiglaTitulo)
					.and(especificacaoIdOrganizacao)
					.and(especificacaoStatus);

		return repository.findAll(filtroPesquisa, pageable)
					.map(projeto -> {
						Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

						ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

						return new ProjetoListaDto(projeto, valorDto.quantia());
					});
	}

	public List<ProjetoPropostoOpcoesDto> listarOpcoesDropdown() {
		return repository.findAll(Sort.by(Sort.Direction.ASC, "titulo"))
					.stream()
					.filter(Projeto::isAtivo)
					.map(projeto -> {
						Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

						ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

						return new ProjetoPropostoOpcoesDto(projeto, valorDto);
					})
					.toList();
	}

	public ProjetoDto buscarPorId(Long id) {
		logger.info("Buscando projeto com id: {}", id);

		Projeto projeto = this.buscar(id);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.buscarPorProjeto(projeto);

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		Set<ProjetoIndicador> indicadores = projetoIndicadorService.buscarPorProjeto(projeto);

		return new ProjetoDto(projeto, valorDto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet),
			this.buscarEquipeElaboracao(projetoPessoaSet),
			this.buscarSubResponsavelProponente(projetoPessoaSet),
			this.buscarIndicadores(indicadores));

	}

	private List<ProjetoIndicadorDto> buscarIndicadores(Set<ProjetoIndicador> projetoPessoaSet) {
		return projetoPessoaSet.stream()
			.map(ProjetoIndicadorDto::new)
			.toList();
	}

	@Transactional
	public ProjetoDto cadastrar(ProjetoForm form, boolean rascunho) {
		
		logger.info("Cadastrando novo projeto");
		logger.info("Dados: {}", form);

		this.validarProjeto(form, true);

		Projeto tempProjeto = new Projeto(form);

		tempProjeto.setCountAno(this.buscarCountAnoFormatado());

		if (rascunho) {
			tempProjeto.setRascunho(true);
			tempProjeto.setStatus(StatusProjetoEnum.EM_ELABORACAO.getValue());
		} else {
			tempProjeto.setRascunho(false);
			tempProjeto.setStatus(StatusProjetoEnum.EM_ANALISE.getValue());
		}

		Projeto projeto = repository.save(tempProjeto); 

		Set<ProjetoPessoa> projetoPessoaSet;

		List<EquipeDto> equipeParaGravar = form.equipeElaboracao();
		List<EquipeDto> equipeElaboracaoValidada = this.validarEquipeElaboracao(form);
		if (!new HashSet<>(form.equipeElaboracao()).equals(new HashSet<>(equipeElaboracaoValidada))) {
			equipeParaGravar = equipeElaboracaoValidada;
		}

		projetoPessoaSet = projetoPessoaService.cadastrar( projeto, form.idResponsavelProponente(), equipeParaGravar );

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.cadastrar(projeto, form.valor(), form.rateio());

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		List<ProjetoIndicadorDto> indicadores = form.indicadoresProjeto();

		logger.info("Projeto cadastrado com sucesso");

		return new ProjetoDto(projeto, valorDto, rateio, 
			this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet), 
			this.buscarSubResponsavelProponente(projetoPessoaSet) ,
			indicadores);
	}


	@Transactional
	public ProjetoDto atualizar(Long id, ProjetoForm form, boolean rascunho) {

		logger.info("Atualizando projeto com id: {}", id);

		this.validarProjeto(form, false);

		Projeto projeto = this.buscar(id);
		projeto.atualizarProjeto(form);

		if (rascunho) {
			projeto.setRascunho(true);
			projeto.setStatus(StatusProjetoEnum.EM_ELABORACAO.getValue());
		} else {
			projeto.setRascunho(false);
			projeto.setStatus(StatusProjetoEnum.EM_ANALISE.getValue());
		}

		Projeto projetoResult = repository.save(projeto);

		Set<ProjetoPessoa> projetoPessoaSet;
		List<EquipeDto> equipeParaGravar = form.equipeElaboracao();
		List<EquipeDto> equipeElaboracaoValidada = this.validarEquipeElaboracao(form);
		if (!new HashSet<>(form.equipeElaboracao()).equals(new HashSet<>(equipeElaboracaoValidada))) {
			equipeParaGravar = equipeElaboracaoValidada;
		}
		projetoPessoaSet = projetoPessoaService.atualizar(projeto, form.idResponsavelProponente(), equipeParaGravar);

		List<ProjetoIndicadorDto> projetoIndicadoresDto = form.indicadoresProjeto();

		Set<ProjetoIndicador> projetoIndicadoresSet = projetoIndicadorService.atualizar(projeto, projetoIndicadoresDto);

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.atualizar(projetoResult, form.valor(), form.rateio());

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		logger.info("Projeto atualizado com sucesso");

		return new ProjetoDto(projetoResult, valorDto, rateio, 
			this.buscarIdResponsavelProponente(projetoPessoaSet), 
			this.buscarEquipeElaboracao(projetoPessoaSet), 
			this.buscarSubResponsavelProponente(projetoPessoaSet),
			this.buscarIndicadores(projetoIndicadoresSet));

	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo projeto com id: {}", id);

		Projeto projeto = this.buscar(id);

		projeto.apagarProjeto();
		repository.saveAndFlush(projeto);
		repository.deleteById(id);

		projetoPessoaService.excluirPorProjeto(projeto);
		localidadeQuantiaService.excluir(projeto);

		logger.info("Projeto excluido com sucesso");
	}

	@Transactional
	public void alterarStatusProjeto(Long id, String status) {
		Projeto projeto = this.buscar(id);

		projeto.setStatus(status);

		repository.save(projeto);
	}

	public List<Long> buscarIdProjetoPropostoList(Programa programa) {
		logger.info("Buscando projetos vinculados ao programa com id: {}", programa.getId());

		return this.buscarProjetosPorPrograma(programa)
					.stream()
					.map(Projeto::getId)
					.toList();
	}

	public List<OpcoesDto> buscarProjetosPropostos(Programa programa) {
		return this.buscarProjetosPorPrograma(programa)
					.stream()
					.map(OpcoesDto::new)
					.toList();
	}

	@Transactional
	public List<Long> vincularProjetosAoPrograma(Programa programa, List<Long> idProjetoPropostoList) {
		logger.info("Vinculando projetos ao programa com id: {}", programa.getId());
		logger.info("Ids dos projetos: {}", idProjetoPropostoList);

		Set<Projeto> projetoPropostoSet = repository.findAllByPrograma(programa);

		if (!projetoPropostoSet.isEmpty()) {
			projetoPropostoSet.forEach(projeto -> {
				if (idProjetoPropostoList.stream().noneMatch(idProjetoProposto -> idProjetoProposto.equals(projeto.getId()))) {
					projeto.setPrograma(null);
				}
			});

			repository.saveAllAndFlush(projetoPropostoSet);
		}

		idProjetoPropostoList.forEach(idProjetoProposto -> {
			Projeto projeto = this.buscar(idProjetoProposto);
			projeto.setPrograma(programa);
			repository.saveAndFlush(projeto);
		});

		logger.info("Projetos vinculados ao programa com sucesso");
		return this.buscarIdProjetoPropostoList(programa);
	}

	@Transactional
	public void desvincularProjetosDoPrograma(Programa programa) {
		logger.info("Desvinculando projetos ao programa com id: {}", programa.getId());

		Set<Projeto> projetoPropostoSet = repository.findAllByPrograma(programa);

		if (!projetoPropostoSet.isEmpty()) {
			projetoPropostoSet.forEach(projeto -> {
				projeto.setPrograma(null);
			});

			repository.saveAllAndFlush(projetoPropostoSet);
		}

		logger.info("Projetos desvinculados do programa com sucesso");
	}


	public String gerarNomeArquivo(Integer idProjeto) {
		Projeto projeto = this.buscar(Long.valueOf(idProjeto));

		if (projeto.getOrganizacao().getCnpj() == null) {
			throw new RelatorioNomeArquivoException("Organização não possui CNPJ.");
		}

		String cnpj = this.formatarCnpj(projeto.getOrganizacao().getCnpj());

		return "PROJETO n. " +
					projeto.getCountAno() + "-" +
					projeto.getOrganizacao().getNomeFantasia() + "-" +
					cnpj;
	}

	public int buscarQuantidadeProjetos() {
		return Integer.parseInt(String.valueOf((repository.count())));
	}

	public BigDecimal buscarSomatorioValorEstimadoProjetos() {
		return localidadeQuantiaService.somarValorEstimadoTodosProjetos();
	}

	private Projeto buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	private Long buscarIdResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
					.filter(ProjetoPessoa::isResponsavelProponente)
					.findFirst()
					.map(projetoPessoa -> projetoPessoa.getPessoa().getId())
					.orElse(null);
	}

	private String buscarSubResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
					.filter(ProjetoPessoa::isResponsavelProponente)
					.findFirst()
					.map(projetoPessoa -> projetoPessoa.getPessoa().getSub())
					.orElse(null);
	}

	private List<EquipeDto> buscarEquipeElaboracao(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
					.filter(Predicate.not(ProjetoPessoa::isResponsavelProponente))
					.map(EquipeDto::new)
					.toList();
	}

	private Set<Projeto> buscarProjetosPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private String formatarCnpj(String cnpj) {
		return cnpj.replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$", "$1.$2.$3/$4-$5");
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}

	private void validarProjeto(ProjetoForm form, boolean isSalvar) {
		List<String> erros = new ArrayList<>();

		boolean checkFormIdOrganizacaoExistePorId = !organizacaoService.existePorId(form.idOrganizacao());
		boolean checkProjetoExistePorSigla = repository.existsBySigla(form.sigla()) && isSalvar;

		if (checkFormIdOrganizacaoExistePorId)
			erros.add("Erro ao encontrar Organização com id " + form.idOrganizacao());

		if (checkProjetoExistePorSigla)
			erros.add("Já existe um projeto cadastrado com essa sigla.");

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}
	}
	
	private List<EquipeDto> validarEquipeElaboracao(ProjetoForm form) {
		List<EquipeDto> equipe = new ArrayList<>();
		for (EquipeDto membro : form.equipeElaboracao()) {
			String sub = membro.subPessoa();
			String id = pessoaService.buscarIdPorSub(sub);
			if (id.isBlank()) {
				logger.info("Pessoa com sub [{}] não encontrada na base do SISCAP, procedendo para criação.", sub);
				var dados = acessoCidadaoService.buscarPessoaPorSub(sub);
				Pessoa pessoa = new Pessoa();
				pessoa.setNome(dados.nome());
				pessoa.setNomeSocial(dados.apelido());
				pessoa.setEmail(dados.email());
				pessoa.setSub(dados.sub());
				pessoa.setApagado(false);
				pessoa.setCriadoEm(LocalDateTime.now());
				pessoa = pessoaService.salvarNovaPessoaAcessoCidadao(pessoa);
				logger.info("Pessoa [{}] criada com sucesso. ID: {}", pessoa.getNome(), pessoa.getId());
				id = pessoa.getId().toString();
			}
			EquipeDto novoMembro = new EquipeDto( Long.valueOf(id), membro.idPapel(), membro.idStatus(), membro.justificativa(), membro.subPessoa() );
			equipe.add(novoMembro);
		}
		return equipe;
	}

}