package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoPropostoSelectDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.service.ArquivosService;
import br.gov.es.siscap.service.ProjetoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

	private final ProjetoService service;
	private final ArquivosService arquivosService;

	@GetMapping
	public Page<ProjetoListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "sigla") Pageable pageable,
				@RequestParam(required = false, defaultValue = "") String search
	) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/select")
	public List<ProjetoPropostoSelectDto> listarSelect() {
		return service.listarSelect();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjetoDto> buscarPorId(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<ProjetoDto> cadastrar(@Valid @RequestBody ProjetoForm form) {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjetoDto> atualizar(@PathVariable @NotNull Long id, @Valid @RequestBody ProjetoForm form) {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable @NotNull Long id) {
		service.excluir(id);
		return ResponseEntity.ok().body("Projeto excluído com sucesso!");
	}


	@GetMapping("/dic/{idProjeto}")
	public ResponseEntity<Resource> gerarDIC(@PathVariable Integer idProjeto) {
		Resource resource = arquivosService.gerarArquivo("DIC", idProjeto);
		String nomeArquivo = service.gerarNomeArquivo(idProjeto);

		String contentType = "application/pdf";

		return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + ".pdf\"")
					.body(resource);
	}
}
