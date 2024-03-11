package br.gov.es.siscap.service;

import br.gov.es.siscap.repository.TipoEntidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoEntidadeService {

    private final TipoEntidadeRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

}
