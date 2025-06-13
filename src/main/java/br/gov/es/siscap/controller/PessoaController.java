package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ResponsavelProponenteOpcoesDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.service.PessoaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import br.gov.es.siscap.service.AcessoCidadaoService;
import br.gov.es.siscap.service.AutenticacaoService;
import br.gov.es.siscap.service.CacheAgentesGovesService;
import br.gov.es.siscap.service.OrganizacaoService;
import br.gov.es.siscap.service.OrganogramaService;
import br.gov.es.siscap.service.PessoaOrganizacaoService;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

	private final Logger logger = LogManager.getLogger(AutenticacaoService.class);
	private final PessoaService service;
	private final OrganizacaoService organizacaoService;
	private final AcessoCidadaoService acessoCidadaoService;
	private final OrganogramaService organogramaService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;

	@Autowired
    private CacheAgentesGovesService cacheService; 

	@Value("${guidGOVES}")
	private String GUID_GOVES;

	@GetMapping
	public Page<PessoaListaDto> listarTodos(
				@PageableDefault(size = 15, sort = "nome") Pageable pageable,
				@RequestParam(required = false, defaultValue = "") String search
	) {
		return service.listarTodos(pageable, search);
	}

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PessoaDto> buscarPorId(@NotNull @PathVariable Long id) throws IOException {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@GetMapping("/sub/{sub}")
	public ResponseEntity<String> buscarPorSub(@NotNull @PathVariable String sub) {
		return ResponseEntity.ok(service.buscarIdPorSub(sub));
	}

	@PostMapping("/syncPorSub/{sub}")
	public ResponseEntity<String> sincronizarPessoaPorSub(@NotNull @PathVariable String sub) throws IOException {
		
		String idPessoaSiscap = service.buscarIdPorSub(sub);
		
		if (idPessoaSiscap.isBlank()) {
			logger.info("Pessoa não encontrada na base do SISCAP, procedendo para criação de uma nova pessoa.");
			idPessoaSiscap = service.sincronizarAgenteCidadaoPessoaSiscap(sub);
		} else {
			logger.info("Pessoa encontrada na base do SISCAP, procedendo atualizacao de dados caso necessário ID pessoa : {}." , idPessoaSiscap );
			service.sincronizarDadosAgentePessoaSiscap(Long.valueOf(idPessoaSiscap),sub);
		}
		
		return ResponseEntity.ok(idPessoaSiscap);
	}

	@PostMapping
	public ResponseEntity<PessoaDto> cadastrar(@Valid @ModelAttribute PessoaForm form)
				throws IOException {
		return new ResponseEntity<>(service.cadastrar(form), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PessoaDto> atualizar(@NotNull @PathVariable Long id, PessoaForm form)
				throws IOException {
		return ResponseEntity.ok(service.atualizar(id, form, null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> excluir(@NotNull @PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.ok("Pessoa excluída com sucesso.");
	}

	@GetMapping("/acesso-cidadao/{cpf}")
	public ResponseEntity<AgentePublicoACDto> buscarPessoaNoAcessoCidadaoPorCpf(@PathVariable String cpf) {
		return ResponseEntity.ok(service.buscarPessoaNoAcessoCidadaoPorCpf(cpf));
	}

	@GetMapping("/responsavel/{orgId}")
	public ResponseEntity<OpcoesDto> buscarResponsavelPorIdOrganizacao(
				@NotNull @PathVariable Long orgId
	) throws IOException {
		return ResponseEntity.ok(service.buscarResponsavelPorIdOrganizacao(orgId));
	}

	@GetMapping("/meu-perfil")
	public ResponseEntity<PessoaDto> buscarMeuPerfil(@NotNull String subNovo) throws IOException {
		return ResponseEntity.ok(service.buscarMeuPerfil(subNovo));
	}

	@PutMapping("/meu-perfil/{id}")
	public ResponseEntity<PessoaDto> atualizarMeuPerfil(@NotNull @PathVariable Long id, PessoaForm form,
	                                                    Authentication auth)
				throws IOException {
		return ResponseEntity.ok(service.atualizar(id, form, auth));
	}

	@GetMapping("/opcoes/{orgId}")
	public List<ResponsavelProponenteOpcoesDto> listarOpcoesDropdownOrganizacao(@NotNull @PathVariable Long orgId) throws IOException { 
		OrganizacaoDto organizacao = organizacaoService.buscarPorId(orgId);
		return service.listarOpcoesDropdownOrganizacao(organizacao.guid());
	}

	@PostMapping("/opcoes/agentesGoves")
	public ResponseEntity<Map<String, Object>> listarOpcoesDropdownAgentesGoves() throws IOException { 		
		cacheService.carregarCache( service.listarOpcoesDropdownTodosAgentesGoves() );
		return ResponseEntity.ok(Map.of(
        "message", "Dados carregados em cache",
        "count", cacheService.getCache().size() ,
        "timestamp", Instant.now()
    	));
	}

	@GetMapping("/opcoes/agentesGoves/filtrar/{termo}")
	public List<ResponsavelProponenteOpcoesDto> filtrarAgentesGoves(@NotNull @PathVariable String termo) {
		List<ResponsavelProponenteOpcoesDto> cache = cacheService.getCache();
		return service.filtrarAgentesGovesPorTermo(termo, cacheService);
	}

	@GetMapping("/opcoes/agentesGoves/sub/{sub}")
	public ResponsavelProponenteOpcoesDto buscarAgentesGovesPorSub(@NotNull @PathVariable String sub) {
		List<ResponsavelProponenteOpcoesDto> cache = cacheService.getCache();
		if (cache == null || cache.isEmpty()) {
			logger.error("Cache vazio ou não inicializado");
		}
		return service.buscarAgentesGovesPorSub(sub, cacheService);
	}

}