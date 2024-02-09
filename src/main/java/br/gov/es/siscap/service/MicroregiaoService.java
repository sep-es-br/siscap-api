package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.MicroregiaoSelectDto;
import br.gov.es.siscap.repository.MicroregiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicroregiaoService {

    private final MicroregiaoRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<MicroregiaoSelectDto> buscarSelect() {
        return repository.findAll().stream().map(MicroregiaoSelectDto::new).collect(Collectors.toList());
    }
}
