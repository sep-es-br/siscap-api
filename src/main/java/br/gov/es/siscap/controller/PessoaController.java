package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.service.ImagemPerfilService;
import br.gov.es.siscap.service.PessoaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService service;
    private final ImagemPerfilService imagemPerfilService;

    @GetMapping
    public Page<PessoaListaDto> listar(@PageableDefault(size = 15) Pageable pageable) {
        return service.listarTodos(pageable);
    }

    @PostMapping
    public ResponseEntity<PessoaDto> cadastrar(@Valid @ModelAttribute PessoaForm form)
            throws IOException {
        PessoaDto dto = service.salvar(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDto> buscar(@NotNull @PathVariable Long id) throws IOException {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/email")
    public ResponseEntity<PessoaDto> buscarPorEmail(@NotNull String email) throws IOException {
        Pessoa pessoa = service.buscarPorEmail(email);
        Resource imagem = imagemPerfilService.buscar(pessoa.getNomeImagem());
        byte[] conteudo = imagem != null ? imagem.getContentAsByteArray() : null;
        return ResponseEntity.ok(new PessoaDto(pessoa, conteudo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaDto> atualizar(@NotNull @PathVariable Long id, PessoaForm form)
            throws IOException {
        return ResponseEntity.ok(service.atualizar(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@NotNull @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok("Pessoa excluída com sucesso.");
    }

    @GetMapping("/select")
    public List<SelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
