package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.PessoaListaDto;
import br.gov.es.siscap.exception.service.ImagemSisCapException;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaUpdateForm;
import br.gov.es.siscap.service.PessoaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService service;

    @GetMapping
    public Page<PessoaListaDto> listar(@PageableDefault(size = 15) Pageable pageable) {
        return service.listarTodos(pageable);
    }

    @PostMapping
    public ResponseEntity<PessoaDto> cadastrar(@Valid @ModelAttribute PessoaForm form, UriComponentsBuilder uriBuilder) {
        PessoaDto dto = service.salvar(form);
        URI uri = uriBuilder.path("/pessoas/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDto> buscar(@NotNull @PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/{id}/imagem-perfil")
    public ResponseEntity<Resource> buscarImagemPerfil(@NotNull @PathVariable Long id, HttpServletRequest request) throws IOException {
        try {
            Resource resource = service.buscarImagemPerfil(id);
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null)
                contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ImagemSisCapException("Não foi possivel encontrar a imagem de perfil da pessoa com id " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaDto> atualizar(@NotNull @PathVariable Long id, PessoaUpdateForm form) {
        return ResponseEntity.ok(service.atualizar(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@NotNull @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok("Pessoa excluída com sucesso.");
    }


}
