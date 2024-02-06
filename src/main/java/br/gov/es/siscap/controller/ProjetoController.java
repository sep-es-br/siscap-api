package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService service;

    /**
     * Método para listar todos os projetos não apagados no banco.
     *
     * @param pageable Atributo padrão do spring para definir quantidade de registros e paginação da listagem.
     * @return Retorna um objeto page que contem a listagem dos registro e mais detalhamento da paginação.
     */
    @GetMapping
    public Page<ProjetoDto> listar(@PageableDefault(size = 15) Pageable pageable) {
        return service.buscarTodos(pageable);
    }

    /**
     * Cria um novo registro de projeto.
     *
     * @param form Formulário com os dados necessários para o cadastro de um novo projeto.
     * @return Retorna o caminho para acessar os detalhes desse projeto e
     * o projeto criado contendo outros campos de controle da aplicação.
     */
    @PostMapping
    public ResponseEntity<ProjetoDto> cadastrar(@RequestBody ProjetoForm form, UriComponentsBuilder uriBuilder) {
        ProjetoDto projeto = service.salvar(form);
        URI uri = uriBuilder.path("/projetos/{id}").buildAndExpand(projeto.id()).toUri();

        return ResponseEntity.created(uri).body(projeto);
    }

}
