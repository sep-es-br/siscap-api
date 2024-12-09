package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.service.PessoaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

	private final PessoaService service;

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
}