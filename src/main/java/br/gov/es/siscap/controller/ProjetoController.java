package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.edocswebapi.EtapasIntegracaoDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.dto.opcoes.ProjetoPropostoOpcoesDto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.service.AsyncExecutorService;
import br.gov.es.siscap.service.IntegraccaoEdocsService;
import br.gov.es.siscap.service.PessoaService;
import br.gov.es.siscap.service.ProjetoService;
import br.gov.es.siscap.service.RelatoriosService;
import br.gov.es.siscap.service.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
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

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

	private final ProjetoService service;
	private final RelatoriosService relatoriosService;
	private final AsyncExecutorService asyncExecutorService;
	private final IntegraccaoEdocsService integracaoEdocsService;
        
        private final TokenService tokenService;
        private final PessoaService pessoaSrv;

	// private final Logger logger = LogManager.getLogger(ProjetoController.class);

	@GetMapping
	public Page<ProjetoListaDto> listarTodos(
			@PageableDefault(size = 15, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(required = false) String siglaOuTitulo,
			@RequestParam(required = false) Long idOrganizacao,
			@RequestParam(required = false) String status) {
		return service.listarTodos(pageable, siglaOuTitulo, idOrganizacao, status);
	}

	@GetMapping("/opcoes")
	public List<ProjetoPropostoOpcoesDto> listarOpcoesDropdown(
		@RequestParam(required = false) boolean elegiveis,
                @RequestParam(required = false) String incluir
	) {
		if ( elegiveis )
			return service.listarDicsElegiveisParaPrograma(incluir);
		else
			return service.listarOpcoesDropdown();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjetoDto> buscarPorId(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<ProjetoDto> cadastrar(@Valid @RequestBody ProjetoForm form,
			@RequestParam(required = false, defaultValue = "false") boolean rascunho,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
            
            
		return new ResponseEntity<>(service.cadastrar(form, rascunho, pessoa), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjetoDto> atualizar(@PathVariable @NotNull Long id, @Valid @RequestBody ProjetoForm form,
			@RequestParam(required = false, defaultValue = "false") boolean rascunho,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		return ResponseEntity.ok(service.atualizar(id, form, rascunho, pessoa));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@PathVariable @NotNull Long id,
			@RequestBody(required = false) Map<String, String> justificativa,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
                
		ProjetoDto projetoSnapShot = service.buscarPorId(id);
		String justificativaEnviada = justificativa != null ? justificativa.get("justificativa") : "";
		if (service.excluir(id, justificativaEnviada, pessoa)) {
			if (justificativaEnviada != null && !justificativaEnviada.isBlank())
				asyncExecutorService.encerrarProcessoEdocs(projetoSnapShot);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Falha ao excluir o projeto.");
		}
		return ResponseEntity.ok().body("Projeto excluído com sucesso!");
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<String> alterarStatusProjeto(@PathVariable @NotNull Long id,
			@RequestBody Map<String, String> status,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
		service.alterarStatusAtualProjetoByIdProjeto(id, status.get("status"), subNovo);
		return ResponseEntity.ok().body("Status do projeto alterado com sucesso!");
	}

	@PostMapping("/{id}/revisar")
	public ResponseEntity<String> enviarProjetoParaRevisao(@PathVariable @NotNull Long id,
			@RequestBody Map<String, String> justificativa,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		service.enviarSolicitacaoRevisaoProjeto(id, justificativa.get("justificativa"), pessoa);
		return ResponseEntity.ok().body("Solicitação de revisão enviada com sucesso!");
	}

	@PostMapping("/{id}/arquivar")
	public ResponseEntity<String> enviarProjetoParaArquivamento(@PathVariable @NotNull Long id,
			@RequestBody Map<String, String> payload,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		service.enviarAvisoArquivamentoProjeto(id, payload.get("justificativa"),
				payload.get("codigoMotivoArquivamento"), pessoa);
		return ResponseEntity.ok().body("Aviso de arquivamento enviada com sucesso!");
	}

	@PostMapping("/{id}/complementar")
	public ResponseEntity<String> enviarProjetoParaComplementacao(
			@PathVariable @NotNull Long id,
			@RequestBody List<ProjetoCamposComplementacaoDto> complementos,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		asyncExecutorService.despacharProcessoOrgaoOrigemEdocs(id, complementos, pessoa);
		return ResponseEntity.ok().body("Aviso de complementação enviada com sucesso!");
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

	@PutMapping("/dic/edocs/autuar/{idProjeto}")
	public ResponseEntity<Resource> assinarAutuarDIC(@PathVariable Long idProjeto,
			@Valid @RequestBody ProjetoForm form,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		service.atualizar(idProjeto, form, false, pessoa);
		asyncExecutorService.executarAutuacaoEdocs(idProjeto, pessoa);
		return ResponseEntity.accepted().build();
	}

	@PutMapping("/dic/edocs/capturarparecer/{idProjeto}")
	public ResponseEntity<Resource> assinarCapturaParecerDIC(@PathVariable Long idProjeto,
			@Valid @RequestBody ProjetoForm form,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		ProjetoDto projetoDto = service.atualizar(idProjeto, form, false, pessoa);
		asyncExecutorService.assinarCapturaParecerDIC(idProjeto, projetoDto.parecerProjetoUsuario().id());
		return ResponseEntity.accepted().build();
	}

	@GetMapping("/dic/edocs/fases/{idProjeto}")
	public ResponseEntity<List<EtapasIntegracaoDto>> integracaoEdocsFases(@PathVariable Long idProjeto) {
		var fases = integracaoEdocsService.consultarFasesIntegracaoEdocsProjeto(idProjeto);
		return ResponseEntity.ok(fases);
	}

	@PutMapping("/dic/edocs/reentranharDIC/{idProjeto}")
	public ResponseEntity<Resource> reentranharDIC(@PathVariable Long idProjeto,
			@Valid @RequestBody ProjetoForm form,
                        @RequestHeader("Authorization") String auth) {
            
		String token = auth.replace("Bearer ", "");
                
                
                String subNovo = this.tokenService.validarToken(token);
                
                Pessoa pessoa = this.pessoaSrv.buscarPorSub(subNovo);
		service.atualizar(idProjeto, form, false, pessoa);
		asyncExecutorService.executarReentranhamentoDicEdocs(idProjeto, pessoa);
		return ResponseEntity.accepted().build();
	}

	@PutMapping("/dic/edocs/entranharpareceres/{idProjeto}")
	public ResponseEntity<Resource> entranharPareceresDIC(@PathVariable Long idProjeto,
			@Valid @RequestBody ProjetoForm form) {
		asyncExecutorService.entranharPareceresDIC(idProjeto);
		return ResponseEntity.accepted().build();
	}

}