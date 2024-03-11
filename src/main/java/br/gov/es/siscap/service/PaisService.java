package br.gov.es.siscap.service;

import br.gov.es.siscap.repository.PaisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaisService {

    private final PaisRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

}
