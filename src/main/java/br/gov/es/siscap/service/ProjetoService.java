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
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProjetoRepository;
import br.gov.es.siscap.specification.ProjetoSpecification;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

	private final ProjetoRepository repository;
	private final ProjetoPessoaService projetoPessoaService;
	private final LocalidadeQuantiaService localidadeQuantiaService;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(ProjetoService.class);

	public Page<ProjetoListaDto> listarTodos(
				Pageable pageable,
				String siglaOuTitulo,
				Long idOrganizacao,
				String status,
				String dataPeriodoInicio,
				String dataPeriodoFim) {

		Specification<Projeto> especificacaoSiglaTitulo = siglaOuTitulo.isBlank() ? null : ProjetoSpecification.filtroSiglaTitulo(siglaOuTitulo);
		Specification<Projeto> especificacaoIdOrganizacao = idOrganizacao == 0 ? null : ProjetoSpecification.filtroIdOrganizacao(idOrganizacao);
		Specification<Projeto> especificacaoStatus = status.equals("Todos") ? null : ProjetoSpecification.filtroStatus(status);
		Specification<Projeto> especificacaoData = ProjetoSpecification.filtroData(dataPeriodoInicio, dataPeriodoFim);

		Specification<Projeto> filtroPesquisa = Specification
					.where(especificacaoSiglaTitulo)
					.and(especificacaoIdOrganizacao)
					.and(especificacaoStatus)
					.and(especificacaoData);

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

		return new ProjetoDto(projeto, valorDto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
	}

	@Transactional
	public ProjetoDto cadastrar(ProjetoForm form, boolean rascunho) {
		logger.info("Cadastrando novo projeto");
		logger.info("Dados: {}", form);

		this.validarProjeto(form, true);

		Projeto tempProjeto = new Projeto(form);

		if (rascunho) {
			tempProjeto.setRascunho(true);
			tempProjeto.setStatus(StatusProjetoEnum.EM_ELABORACAO.getValue());
		} else {
			tempProjeto.setRascunho(false);
			tempProjeto.setStatus(StatusProjetoEnum.EM_ANALISE.getValue());
		}

		Projeto projeto = repository.save(tempProjeto);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.cadastrar(projeto, form.idResponsavelProponente(), form.equipeElaboracao());

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.cadastrar(projeto, form.valor(), form.rateio());

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		logger.info("Projeto cadastrado com sucesso");
		return new ProjetoDto(projeto, valorDto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
	}


	@Transactional
	public ProjetoDto atualizar(Long id, ProjetoForm form, boolean rascunho) {
		logger.info("Atualizando projeto com id: {}", id);
		logger.info("Dados: {}", form);

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

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.atualizar(projetoResult, form.idResponsavelProponente(), form.equipeElaboracao());

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.atualizar(projetoResult, form.valor(), form.rateio());

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		logger.info("Projeto atualizado com sucesso");
		return new ProjetoDto(projetoResult, valorDto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
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
					projeto.getId() + "/" +
					projeto.getCriadoEm().getYear() + "-" +
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
}