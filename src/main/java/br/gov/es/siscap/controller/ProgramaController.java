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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programas")
@RequiredArgsConstructor
public class ProgramaController {

	private final ProgramaService service;

	@GetMapping
	public Page<ProgramaListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "sigla") Pageable pageable,
				@RequestParam(required = false, defaultValue = "") String search
	) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/{id}")
	public ProgramaDto buscarPorId(@NotNull @Positive @PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<ProgramaDto> cadastrar(
				@Valid @RequestBody ProgramaForm form
	) {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProgramaDto> atualizar(
				@NotNull @Positive @PathVariable Long id,
				@Valid @RequestBody ProgramaForm form
	) {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(
				@NotNull @Positive @PathVariable Long id
	) {
		service.excluir(id);
		return ResponseEntity.ok("Programa excluido com sucesso!");
	}
}
