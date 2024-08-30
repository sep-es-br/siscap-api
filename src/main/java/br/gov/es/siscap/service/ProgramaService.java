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
		return repository.findAll(pageable)
					.map(programa -> new ProgramaListaDto(programa, programaValorService.buscarPorPrograma(programa)));
	}

	public ProgramaDto buscarPorIdPrograma(Long idPrograma) {
		Programa programa = this.buscarPorId(idPrograma);

		List<EquipeDto> equipeCaptacao = programaPessoaService.buscarPorPrograma(programa);

		List<ProjetoPropostoDto> projetosPropostos = programaProjetoService.buscarPorPrograma(programa);

		ValorDto valor = programaValorService.buscarPorPrograma(programa);

		return new ProgramaDto(programa, equipeCaptacao, projetosPropostos, valor);
	}

	@Transactional
	public ProgramaDto salvar(ProgramaForm form) {
		logger.info("Cadastrar novo programa: {}.", form);

		Programa programa = repository.save(new Programa(form));

		List<EquipeDto> equipeCaptacao = programaPessoaService.salvar(programa, form.equipeCaptacao());

		List<ProjetoPropostoDto> projetosPropostos = programaProjetoService.salvar(programa, form.projetosPropostos());

		ValorDto valor = programaValorService.salvar(programa, form.valor());

		logger.info("Cadastro de programa finalizado!");
		return new ProgramaDto(programa, equipeCaptacao, projetosPropostos, valor);
	}

	@Transactional
	public ProgramaDto atualizar(Long idPrograma, ProgramaForm form) {
		logger.info("Atualizar programa de id {}: {}.", idPrograma, form);
		Programa programa = this.buscarPorId(idPrograma);

		programa.atualizar(form);

		Programa programaResult = repository.save(programa);

		List<EquipeDto> equipeCaptacaoAtualizado = programaPessoaService.atualizar(programaResult, form.equipeCaptacao());

		List<ProjetoPropostoDto> projetosPropostosAtualizado = programaProjetoService.atualizar(programaResult, form.projetosPropostos());

		ValorDto valorAtualizado = programaValorService.atualizar(programaResult, form.valor());

		logger.info("Atualização do programa de id: {} finalizada!", programaResult.getId());
		return new ProgramaDto(programaResult, equipeCaptacaoAtualizado, projetosPropostosAtualizado, valorAtualizado);

	}

	@Transactional
	public void excluir(Long idPrograma) {
		logger.info("Excluir programa {}.", idPrograma);
		Programa programa = this.buscarPorId(idPrograma);
		programa.apagar();
		repository.saveAndFlush(programa);
		repository.deleteById(idPrograma);

		programaPessoaService.excluirPorPrograma(programa);
		programaProjetoService.excluirPorPrograma(programa);
		programaValorService.excluir(programa);

		logger.info("Exclusão do programa com id {} finalizada!", idPrograma);
	}


	private Programa buscarPorId(Long idPrograma) {
		return repository.findById(idPrograma).orElseThrow(() -> new RuntimeException("Programa não encontrado"));
	}
}
