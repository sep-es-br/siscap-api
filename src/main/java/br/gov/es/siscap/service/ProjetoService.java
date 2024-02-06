package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.entity.Projeto;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository repository;

    public ProjetoDto salvar(ProjetoForm form) {
        Projeto projeto = repository.save(new Projeto(form));
        return new ProjetoDto(projeto);
    }

    public Page<ProjetoDto> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ProjetoDto::new);
    }
}
