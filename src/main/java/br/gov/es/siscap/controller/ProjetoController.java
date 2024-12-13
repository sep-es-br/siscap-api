package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.opcoes.ProjetoPropostoOpcoesDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.service.ProjetoService;
import br.gov.es.siscap.service.RelatoriosService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private final RelatoriosService relatoriosService;

	@GetMapping
	public Page<ProjetoListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable,
				@RequestParam(required = false) String siglaOuTitulo,
				@RequestParam(required = false) Long idOrganizacao,
				@RequestParam(required = false) String status,
				@RequestParam(required = false) String dataPeriodoInicio,
				@RequestParam(required = false) String dataPeriodoFim
	) {
		return service.listarTodos(pageable, siglaOuTitulo, idOrganizacao, status, dataPeriodoInicio, dataPeriodoFim);
	}

	@GetMapping("/opcoes")
	public List<ProjetoPropostoOpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjetoDto> buscarPorId(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<ProjetoDto> cadastrar(@Valid @RequestBody ProjetoForm form, @RequestParam(required = false, defaultValue = "false") boolean rascunho) {
		return new ResponseEntity<>(service.cadastrar(form, rascunho), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjetoDto> atualizar(@PathVariable @NotNull Long id, @Valid @RequestBody ProjetoForm form, @RequestParam(required = false, defaultValue = "false") boolean rascunho) {
		return ResponseEntity.ok(service.atualizar(id, form, rascunho));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable @NotNull Long id) {
		service.excluir(id);
		return ResponseEntity.ok().body("Projeto excluído com sucesso!");
	}


	@GetMapping("/dic/{idProjeto}")
	public ResponseEntity<Resource> gerarDIC(@PathVariable Integer idProjeto) {
		Resource resource = relatoriosService.gerarArquivo("DIC", idProjeto);
		String nomeArquivo = service.gerarNomeArquivo(idProjeto);

		String contentType = "application/pdf";

		return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + ".pdf\"")
					.body(resource);
	}
}