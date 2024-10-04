package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProjetoPropostoDto;
import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.repository.ProgramaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaService {

	private final ProgramaRepository repository;
	private final ProgramaPessoaService programaPessoaService;
	private final ProgramaProjetoService programaProjetoService;
	private final ProgramaValorService programaValorService;
	private final Logger logger = LogManager.getLogger(ProgramaService.class);

	public Page<ProgramaListaDto> listarTodos(Pageable pageable) {
		logger.info("Buscando todos os programas");

		return repository.findAll(pageable)
					.map(programa -> new ProgramaListaDto(programa, programaValorService.buscarPorPrograma(programa)));
	}

	public ProgramaDto buscarPorId(Long id) {
		logger.info("Buscando programa com id: {}", id);

		Programa programa = this.buscar(id);

		List<EquipeDto> equipeCaptacao = programaPessoaService.buscarPorPrograma(programa);

		List<ProjetoPropostoDto> projetosPropostos = programaProjetoService.buscarPorPrograma(programa);

		ValorDto valor = programaValorService.buscarPorPrograma(programa);

		return new ProgramaDto(programa, equipeCaptacao, projetosPropostos, valor);
	}

	@Transactional
	public ProgramaDto cadastrar(ProgramaForm form) {
		logger.info("Cadastrando novo programa");
		logger.info("Dados: {}", form);

		Programa programa = repository.save(new Programa(form));

		List<EquipeDto> equipeCaptacao = programaPessoaService.cadastrar(programa, form.equipeCaptacao());

		List<ProjetoPropostoDto> projetosPropostos = programaProjetoService.cadastrar(programa, form.projetosPropostos());

		ValorDto valor = programaValorService.cadastrar(programa, form.valor());

		logger.info("Programa cadastrado com sucesso");
		return new ProgramaDto(programa, equipeCaptacao, projetosPropostos, valor);
	}

	@Transactional
	public ProgramaDto atualizar(Long id, ProgramaForm form) {
		logger.info("Atualizando programa com id: {}", id);
		logger.info("Dados: {}", form);

		Programa programa = this.buscar(id);

		programa.atualizar(form);

		Programa programaResult = repository.save(programa);

		List<EquipeDto> equipeCaptacaoAtualizado = programaPessoaService.atualizar(programaResult, form.equipeCaptacao());

		List<ProjetoPropostoDto> projetosPropostosAtualizado = programaProjetoService.atualizar(programaResult, form.projetosPropostos());

		ValorDto valorAtualizado = programaValorService.atualizar(programaResult, form.valor());

		logger.info("Programa atualizado com sucesso");
		return new ProgramaDto(programaResult, equipeCaptacaoAtualizado, projetosPropostosAtualizado, valorAtualizado);

	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo programa com id: {}", id);

		Programa programa = this.buscar(id);
		programa.apagar();
		repository.saveAndFlush(programa);

		programaPessoaService.excluirPorPrograma(programa);
		programaProjetoService.excluirPorPrograma(programa);
		programaValorService.excluir(programa);

		logger.info("Programa excluído com sucesso");
	}


	private Programa buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
	}
}
