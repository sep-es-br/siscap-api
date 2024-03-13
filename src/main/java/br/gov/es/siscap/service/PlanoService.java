package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PlanoSelectDto;
import br.gov.es.siscap.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanoService {

    public final PlanoRepository repository;

    public List<PlanoSelectDto> buscarSelect() {
        return repository.findAll().stream().map(PlanoSelectDto::new).toList();
    }

}
