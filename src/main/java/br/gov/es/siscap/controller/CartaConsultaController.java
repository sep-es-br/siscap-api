package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.CartaConsultaDetalhesDto;
import br.gov.es.siscap.dto.CartaConsultaDto;
import br.gov.es.siscap.dto.listagem.CartaConsultaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.form.CartaConsultaForm;
import br.gov.es.siscap.service.CartaConsultaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartas-consulta")
@RequiredArgsConstructor
public class CartaConsultaController {

	private final CartaConsultaService service;

	@GetMapping
	public Page<CartaConsultaListaDto> listarTodos(@PageableDefault(size = 15, sort = "id") Pageable pageable) {
		return service.listarTodos(pageable);
	}

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}

	@GetMapping("/{id}")
	public ResponseEntity<CartaConsultaDto> buscarPorId(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@GetMapping("/{id}/detalhes")
	public ResponseEntity<CartaConsultaDetalhesDto> buscarDetalhesPorId(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(service.buscarDetalhesPorId(id));
	}

	@PostMapping
	public ResponseEntity<CartaConsultaDto> cadastrar(@Valid @RequestBody CartaConsultaForm form) {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CartaConsultaDto> atualizar(@PathVariable @NotNull Long id, @Valid @RequestBody CartaConsultaForm form) {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable @NotNull Long id) {
		service.excluir(id);
		return ResponseEntity.ok("Carta de consulta excluída com sucesso");
	}
}