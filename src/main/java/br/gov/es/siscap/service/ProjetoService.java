package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.exception.RelatorioNomeArquivoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCidade;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

	private final ProjetoRepository repository;
	private final ProjetoPessoaService projetoPessoaService;
	private final ProjetoCidadeService projetoCidadeService;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(ProjetoService.class);

	@Transactional
	public ProjetoDto salvar(ProjetoForm form) {
		logger.info("Cadastrar novo projeto: {}.", form);
		validarProjeto(form, true);
		Projeto projeto = repository.save(new Projeto(form));

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.salvar(projeto, form.idResponsavelProponente(), form.equipeElaboracao());

		Set<ProjetoCidade> projetoCidadeSet = projetoCidadeService.salvar(projeto, form.rateio());

		logger.info("Cadastro de projeto finalizado!");
		return ProjetoDto.montar(projeto, projetoPessoaSet, projetoCidadeSet);
	}

	public Page<ProjetoListaDto> listarTodos(Pageable pageable) {
		return repository
					.findAll(pageable)
					.map(projeto -> new ProjetoListaDto(projeto, projetoCidadeService.listarNomesCidadesPorProjeto(projeto)));
	}

	@Transactional
	public ProjetoDto atualizar(Long id, ProjetoForm form) {
		logger.info("Atualizar projeto de id {}: {}.", id, form);
		validarProjeto(form, false);
		Projeto projeto = buscarPorId(id);
		projeto.atualizarProjeto(form);
		Projeto projetoResult = repository.save(projeto);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.atualizar(projetoResult, form.idResponsavelProponente(), form.equipeElaboracao());

		Set<ProjetoCidade> projetoCidadeSet = projetoCidadeService.atualizar(projetoResult, form.rateio());

		logger.info("Atualização do projeto de id: {} finalizada!", projetoResult.getId());
		return ProjetoDto.montar(projeto, projetoPessoaSet, projetoCidadeSet);
	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluir projeto {}.", id);
		Projeto projeto = buscarPorId(id);
		projeto.apagar();
		repository.saveAndFlush(projeto);
		repository.deleteById(id);

		projetoPessoaService.excluirPorProjeto(projeto);
		projetoCidadeService.excluir(projeto);

		logger.info("Exclusão do projeto com id {} finalizada!", id);
	}

	public ProjetoDto buscar(Long id) {
		Projeto projeto = this.buscarPorId(id);

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.buscarPorProjeto(projeto);

		Set<ProjetoCidade> projetoCidadeSet = projetoCidadeService.buscarPorProjeto(projeto);

		return ProjetoDto.montar(projeto, projetoPessoaSet, projetoCidadeSet);
	}

	public String gerarNomeArquivo(Integer idProjeto) {
		Projeto projeto = buscarPorId(Long.valueOf(idProjeto));

		if(projeto.getOrganizacao().getCnpj() == null) {
			throw new RelatorioNomeArquivoException("Organização não possui CNPJ.");
		}

		String cnpj = formatarCnpj(projeto.getOrganizacao().getCnpj());

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

	private Projeto buscarPorId(Long id) {
		return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	private void validarProjeto(ProjetoForm form, boolean isSalvar) {
		List<String> erros = new ArrayList<>();

		if (!organizacaoService.existePorId(form.idOrganizacao())) {
			erros.add("Erro ao encontrar Organização com id " + form.idOrganizacao());
		}

		if (repository.existsBySigla(form.sigla()) && isSalvar)
			erros.add("Já existe um projeto cadastrado com essa sigla.");

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}
	}

	private String formatarCnpj(String cnpj) {
		return cnpj.replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$", "$1.$2.$3/$4-$5");
	}

}
