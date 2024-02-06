package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.entity.Projeto;
import br.gov.es.siscap.exception.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository repository;

    @Transactional
    public ProjetoDto salvar(ProjetoForm form) {
        Projeto projeto = repository.save(new Projeto(form));
        return new ProjetoDto(projeto);
    }

    public Page<ProjetoDto> buscarTodos(Pageable pageable) {
        return repository.findAllByApagadoEmIsNull(pageable).map(ProjetoDto::new);
    }

    @Transactional
    public void excluir(Integer id) {
        Projeto projeto = repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
        projeto.setApagadoEm(LocalDateTime.now());
    }
}
