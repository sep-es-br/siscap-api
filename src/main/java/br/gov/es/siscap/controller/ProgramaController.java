package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.service.ProgramaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programas")
@RequiredArgsConstructor
public class ProgramaController {

	private final ProgramaService service;

	@GetMapping
	public Page<ProgramaListaDto> listarTodos(@PageableDefault(size = 15, sort = "sigla") Pageable pageable) {
		return service.listarTodos(pageable);
	}

	@GetMapping("/{idPrograma}")
	public ProgramaDto buscarPorIdPrograma(@NotNull @Positive @PathVariable Long idPrograma) {
		return service.buscarPorIdPrograma(idPrograma);
	}

	@PostMapping
	public ResponseEntity<ProgramaDto> cadastrarPrograma(
				@Valid @RequestBody ProgramaForm form
	) {
		ProgramaDto responseProgramaDto = service.salvar(form);
		return ResponseEntity.ok(responseProgramaDto);
	}

	@PutMapping("/{idPrograma}")
	public ResponseEntity<ProgramaDto> atualizarPrograma(
				@NotNull @Positive @PathVariable Long idPrograma,
				@Valid @RequestBody ProgramaForm form
	) {
		ProgramaDto responseProgramaDto = service.atualizar(idPrograma, form);
		return ResponseEntity.ok(responseProgramaDto);
	}

	@DeleteMapping("/{idPrograma}")
	public ResponseEntity<String> excluirPrograma(
				@NotNull @Positive @PathVariable Long idPrograma
	) {
		service.excluir(idPrograma);
		return ResponseEntity.ok("Programa deletado com sucesso!");
	}
}
