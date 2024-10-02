package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.service.OrganizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/organizacoes")
@RequiredArgsConstructor
public class OrganizacaoController {

	private final OrganizacaoService service;

	@GetMapping
	public Page<OrganizacaoListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "nomeFantasia") Pageable pageable,
				@RequestParam(required = false, defaultValue = "") String search
	) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/select")
	public List<SelectDto> listarSelect(
				@RequestParam(required = false) Long filtroTipoOrganizacao
	) {
		return service.listarSelect(filtroTipoOrganizacao);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrganizacaoDto> buscarPorId(@PathVariable Long id) throws IOException {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<OrganizacaoDto> cadastrar(@Valid @ModelAttribute OrganizacaoForm form) throws IOException {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<OrganizacaoDto> atualizar(@PathVariable Long id, OrganizacaoForm form) throws IOException {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.ok("Organização excluída com sucesso!");
	}
}
