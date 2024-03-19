package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PaisSelectDto;
import br.gov.es.siscap.repository.PaisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaisService {

    private final PaisRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<PaisSelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(PaisSelectDto::new).toList();
    }
}
