package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.listagem.ProgramaListaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.form.ProgramaForm;
import br.gov.es.siscap.service.ProgramaService;
import br.gov.es.siscap.service.RelatoriosService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
@RequestMapping("/programas")
@RequiredArgsConstructor
public class ProgramaController {

	private final ProgramaService service;

	private final RelatoriosService relatoriosService;

	@GetMapping
	public Page<ProgramaListaDto> listarTodos(
			@PageableDefault(size = 15, sort = "sigla") Pageable pageable,
			@RequestParam(required = false, defaultValue = "") String search) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}

	@GetMapping("/{id}")
	public ProgramaDto buscarPorId(@NotNull @Positive @PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<ProgramaDto> cadastrar(
			@Valid @RequestBody ProgramaForm form) {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProgramaDto> atualizar(
			@NotNull @Positive @PathVariable Long id,
			@Valid @RequestBody ProgramaForm form) {
		return ResponseEntity.ok(service.atualizar(id, form));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(
			@NotNull @Positive @PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.ok("Programa excluido com sucesso!");
	}

	@PutMapping("/programa/{idPrograma}/edocs/solicitarassinaturas")
	public ResponseEntity<Resource> solicitarAssinaturasProgramaEdocs(@PathVariable Long idPrograma) {
		service.criarArquivoProgramaEdocsAssinaturasPendentes(idPrograma);
		return ResponseEntity.accepted().build();
	}

	@GetMapping("/programa/{idPrograma}/baixar-pdf")
	public ResponseEntity<Resource> gerarPDFPrograma(@PathVariable Integer idPrograma) {
		Resource resource = relatoriosService.gerarArquivoPrograma("PROGRAMA", idPrograma);
		String nomeArquivo = service.gerarNomeArquivo(idPrograma.longValue());
		String contentType = "application/pdf";
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + ".pdf\"")
				.body(resource);
	}

	@PutMapping("/programa/{idPrograma}/edocs/assinar")
	public Mono<ResponseEntity<Void>> assinarProgramaEdocs(@PathVariable Long idPrograma,
			@Valid @RequestBody String subAssinante) {
		return service.assinarProgramaEdocs(idPrograma, subAssinante)
				.thenReturn(ResponseEntity.accepted().build());
	}

}