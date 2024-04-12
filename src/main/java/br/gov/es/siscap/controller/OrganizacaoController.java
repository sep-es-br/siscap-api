package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.form.OrganizacaoUpdateForm;
import br.gov.es.siscap.service.OrganizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<OrganizacaoListaDto> listar(Pageable pageable) {
        return service.listarTodos(pageable);
    }

    @PostMapping
    public ResponseEntity<OrganizacaoDto> cadastrar(@Valid @ModelAttribute OrganizacaoForm form) throws IOException {
        OrganizacaoDto dto = service.salvar(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok().body("Organização excluída com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizacaoDto> atualizar(@PathVariable Long id, OrganizacaoUpdateForm form) throws IOException {
        return ResponseEntity.ok(service.atualizar(id, form));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizacaoDto> buscar(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/select")
    public List<SelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
