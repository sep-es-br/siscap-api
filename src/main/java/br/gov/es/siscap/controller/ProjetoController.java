package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.service.ProjetoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService service;

    /**
     * Método para listar todos os projetos no banco.
     *
     * @param pageable Atributo padrão do spring para definir quantidade de registros e paginação da listagem.
     * @return Retorna um objeto page que contem a listagem dos registro e mais detalhamento da paginação.
     */
    @GetMapping
    public Page<ProjetoListaDto> listar(@PageableDefault(size = 15) Pageable pageable) {
        return service.listarTodos(pageable);
    }

    /**
     * Cria um novo registro de projeto.
     *
     * @param form Formulário com os dados necessários para o cadastro de um novo projeto.
     * @return Retorna o caminho para acessar os detalhes desse projeto e
     * o projeto criado contendo outros campos de controle da aplicação.
     */
    @PostMapping
    public ResponseEntity<ProjetoDto> cadastrar(@Valid @RequestBody ProjetoForm form) {
        ProjetoDto projeto = service.salvar(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(projeto);
    }

    /**
     * Atualiza um projeto já existente no banco de dados.
     *
     * @param id   O id do projeto que vai ser alterado.
     * @param form Formulário com os campos que serão modificados.
     *             Campos não modificados, deverão ser nulos no formulário
     * @return O projeto alterado com todos os campos.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjetoDto> atualizar(@PathVariable @NotNull Long id,
                                                @Valid @RequestBody ProjetoForm form) {
        ProjetoDto dto = service.atualizar(id, form);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Exclui logicamente o registro.
     *
     * @param id id do projeto que deseja excluir.
     * @return A confirmação de exclusão ou erro ao exlcuir.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable @NotNull Long id) {
        service.excluir(id);
        return ResponseEntity.ok().body("Projeto excluído com sucesso!");
    }

    /**
     * Detalhar o Projeto com todos os dados.
     *
     * @param id "Id" do projeto que deseja detalhar.
     * @return O DTO completo do Projeto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoDto> buscar(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

}
