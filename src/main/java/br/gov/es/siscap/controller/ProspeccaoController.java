package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.ProspeccaoDto;
import br.gov.es.siscap.dto.listagem.ProspeccaoListaDto;
import br.gov.es.siscap.form.ProspeccaoForm;
import br.gov.es.siscap.service.ProspeccaoService;
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
@RequestMapping("/prospeccoes")
@RequiredArgsConstructor
public class ProspeccaoController {

	private final ProspeccaoService service;

	@GetMapping
	public Page<ProspeccaoListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "id") Pageable pageable,
				@RequestParam(required = false, defaultValue = "") String search
	) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/{id}")
	public ProspeccaoDto buscarPorId(@NotNull @Positive @PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@GetMapping("/{id}/detalhes")
	public ResponseEntity<ProspeccaoDetalhesDto> buscarDetalhesPorId(@NotNull @Positive @PathVariable Long id) {
		return ResponseEntity.ok(service.buscarDetalhesPorId(id));
	}

	@PostMapping
	public ResponseEntity<ProspeccaoDto> cadastrar(@Valid @RequestBody ProspeccaoForm form) {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProspeccaoDto> atualizar(
				@NotNull @Positive @PathVariable Long id,
				@Valid @RequestBody ProspeccaoForm form
	) {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@NotNull @Positive @PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.ok("Prospecção excluída com sucesso!");
	}
}