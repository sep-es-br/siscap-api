package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EntidadeSelectDto;
import br.gov.es.siscap.repository.EntidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntidadeService {

    private final EntidadeRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<EntidadeSelectDto> buscarSelect() {
        return repository.findAll().stream().map(EntidadeSelectDto::new).collect(Collectors.toList());
    }
}
