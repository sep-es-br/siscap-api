package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.repository.ProgramaRepository;
import br.gov.es.siscap.utils.FormatadorCountAno;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaService {

	private final ProgramaRepository repository;
	private final ProjetoService projetoService;
	private final ProgramaPessoaService programaPessoaService;
	private final Logger logger = LogManager.getLogger(ProgramaService.class);

	public Page<ProgramaListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todos os programas");

		return repository.paginarProgramasPorFiltroPesquisaSimples(search, pageable)
					.map(ProgramaListaDto::new);
	}

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().map(OpcoesDto::new).toList();
	}

	public ProgramaDto buscarPorId(Long id) {
		logger.info("Buscando programa com id: {}", id);

		Programa programa = this.buscar(id);

		List<EquipeDto> equipeCaptacao = programaPessoaService.buscarPorPrograma(programa);

		List<Long> idProjetoPropostoList = projetoService.buscarIdProjetoPropostoList(programa);

		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList);
	}

	@Transactional
	public ProgramaDto cadastrar(ProgramaForm form) {
		logger.info("Cadastrando novo programa");
		logger.info("Dados: {}", form);

		Programa tempPrograma = new Programa(form);

		tempPrograma.setCountAno(buscarCountAnoFormatado());

		Programa programa = repository.save(tempPrograma);

		List<EquipeDto> equipeCaptacao = programaPessoaService.cadastrar(programa, form.equipeCaptacao());

		List<Long> idProjetoPropostoList = projetoService.vincularProjetosAoPrograma(programa, form.idProjetoPropostoList());

		logger.info("Programa cadastrado com sucesso");
		return new ProgramaDto(programa, equipeCaptacao, idProjetoPropostoList);
	}

	@Transactional
	public ProgramaDto atualizar(Long id, ProgramaForm form) {
		logger.info("Atualizando programa com id: {}", id);
		logger.info("Dados: {}", form);

		Programa programa = this.buscar(id);

		programa.atualizar(form);

		Programa programaResult = repository.save(programa);

		List<EquipeDto> equipeCaptacao = programaPessoaService.atualizar(programaResult, form.equipeCaptacao());

		List<Long> idProjetoPropostoList = projetoService.vincularProjetosAoPrograma(programaResult, form.idProjetoPropostoList());

		logger.info("Programa atualizado com sucesso");
		return new ProgramaDto(programaResult, equipeCaptacao, idProjetoPropostoList);

	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo programa com id: {}", id);

		Programa programa = this.buscar(id);
		programa.apagar();
		repository.saveAndFlush(programa);

		programaPessoaService.excluirPorPrograma(programa);
		projetoService.desvincularProjetosDoPrograma(programa);

		logger.info("Programa excluído com sucesso");
	}

	private Programa buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}
}