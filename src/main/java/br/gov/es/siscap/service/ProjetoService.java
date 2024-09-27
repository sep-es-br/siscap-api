package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.*;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.exception.RelatorioNomeArquivoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private final ProjetoRateioService projetoRateioService;
	private final ProgramaProjetoService programaProjetoService;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(ProjetoService.class);

	// IMPLEMENTAR METODO DE PESQUISA SIMPLES UTILIZANDO ARGUMENTO search
	public Page<ProjetoListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todos os projetos");

		return repository
					.findAll(pageable)
					.map(projeto -> new ProjetoListaDto(projeto, projetoRateioService.listarNomesMicrorregioesRateio(projeto)));
	}

	public List<ProjetoPropostoSelectDto> listarSelect() {
		return repository.findAll(Sort.by(Sort.Direction.ASC, "titulo"))
					.stream()
					.filter(Projeto::isAtivo)
					.map(ProjetoPropostoSelectDto::new).toList();
	}

	public ProjetoDto buscarPorId(Long id) {
		logger.info("Buscando projeto com id: {}", id);

		Projeto projeto = this.buscar(id);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.buscarPorProjeto(projeto);

		RateioDto rateio = projetoRateioService.buscarPorProjeto(projeto);

		return new ProjetoDto(projeto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
	}

	@Transactional
	public ProjetoDto cadastrar(ProjetoForm form) {
		logger.info("Cadastrando novo projeto");
		logger.info("Dados: {}", form);

		this.validarProjeto(form, true);

		Projeto projeto = repository.save(new Projeto(form));

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.salvar(projeto, form.idResponsavelProponente(), form.equipeElaboracao());

		RateioDto rateio = projetoRateioService.salvar(projeto, form.rateio());

		logger.info("Projeto cadastrado com sucesso");
		return new ProjetoDto(projeto, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
	}


	@Transactional
	public ProjetoDto atualizar(Long id, ProjetoForm form) {
		logger.info("Atualizando projeto com id: {}", id);
		logger.info("Dados: {}", form);

		this.validarProjeto(form, false);

		Projeto projeto = this.buscar(id);
		projeto.atualizarProjeto(form);
		Projeto projetoResult = repository.save(projeto);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.atualizar(projetoResult, form.idResponsavelProponente(), form.equipeElaboracao());

		RateioDto rateio = projetoRateioService.atualizar(projetoResult, form.rateio());

		logger.info("Projeto atualizado com sucesso");
		return new ProjetoDto(projetoResult, rateio, this.buscarIdResponsavelProponente(projetoPessoaSet), this.buscarEquipeElaboracao(projetoPessoaSet));
	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo projeto com id: {}", id);

		Projeto projeto = this.buscar(id);

		projeto.apagarProjeto();
		repository.saveAndFlush(projeto);
		repository.deleteById(id);

		projetoPessoaService.excluirPorProjeto(projeto);
		projetoRateioService.excluirPorProjeto(projeto);
		programaProjetoService.excluirPorProjeto(projeto);

		logger.info("Projeto excluido com sucesso");
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
		return repository.somarValorEstimadoTodosProjetos();
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
