package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.repository.MicrorregiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicrorregiaoService {

    private final MicrorregiaoRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<SelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(SelectDto::new).toList();
    }
}
