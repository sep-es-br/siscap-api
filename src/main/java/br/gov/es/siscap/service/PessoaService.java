package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaUpdateForm;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaService {

    private final PessoaRepository repository;
    private final ImagemPerfilService imagemPerfilService;

    @Transactional
    public PessoaDto salvar(PessoaForm form) {
        String caminhoImagem = imagemPerfilService.salvar(form.imagemPerfil());
        Pessoa pessoa = repository.save(new Pessoa(form, caminhoImagem));
        return new PessoaDto(pessoa);
    }

    public PessoaDto buscar(Long id) {
        Pessoa pessoa = buscarPorId(id);
        return new PessoaDto(pessoa);
    }

    public Page<PessoaDto> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(PessoaDto::new);
    }

    public Resource buscarImagemPerfil(Long id) {
        Pessoa pessoa = buscarPorId(id);
        return imagemPerfilService.buscar(pessoa.getCaminhoImagem());
    }

    @Transactional
    public PessoaDto atualizar(Long id, PessoaUpdateForm form) {
        String caminhoImagem;
        Pessoa pessoa = buscarPorId(id);
        pessoa.atualizarPessoa(form);
        if (form.imagemPerfil() != null) {
            imagemPerfilService.apagar(pessoa.getCaminhoImagem());
            caminhoImagem = imagemPerfilService.salvar(form.imagemPerfil());
            pessoa.atualizarImagemPerfil(caminhoImagem);
        }
        return new PessoaDto(pessoa);
    }

    @Transactional
    public void excluir(Long id) {
        Pessoa pessoa = buscarPorId(id);
        repository.deleteById(id);
        imagemPerfilService.apagar(pessoa.getCaminhoImagem());
        pessoa.setAtualizadoEm(LocalDateTime.now());
    }

    private Pessoa buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new PessoaNaoEncontradoException(id));
    }
}
