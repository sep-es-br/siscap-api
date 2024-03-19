package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.TipoEntidadeSelectDto;
import br.gov.es.siscap.repository.TipoEntidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoEntidadeService {

    private final TipoEntidadeRepository repository;

    public List<TipoEntidadeSelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "tipo")).stream().map(TipoEntidadeSelectDto::new).toList();
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

}
