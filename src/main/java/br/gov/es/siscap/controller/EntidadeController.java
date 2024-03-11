package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.EntidadeDto;
import br.gov.es.siscap.dto.EntidadeListaDto;
import br.gov.es.siscap.dto.EntidadeSelectDto;
import br.gov.es.siscap.form.EntidadeForm;
import br.gov.es.siscap.form.EntidadeUpdateForm;
import br.gov.es.siscap.service.EntidadeService;
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
@RequestMapping("/entidades")
@RequiredArgsConstructor
public class EntidadeController {

    private final EntidadeService service;

    @GetMapping
    public Page<EntidadeListaDto> listar(Pageable pageable) {
        return service.listarTodos(pageable);
    }

    @PostMapping
    public ResponseEntity<EntidadeDto> cadastrar(@Valid @ModelAttribute EntidadeForm form) throws IOException {
        EntidadeDto dto = service.salvar(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok().body("Entidade excluída com sucesso!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntidadeDto> atualizar(@PathVariable Long id, EntidadeUpdateForm form) throws IOException {
        return ResponseEntity.ok(service.atualizar(id, form));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadeDto> buscar(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/select")
    public List<EntidadeSelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
