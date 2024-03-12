package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.CidadeSelectDto;
import br.gov.es.siscap.enums.FiltroCidadeEnum;
import br.gov.es.siscap.models.Estado;
import br.gov.es.siscap.models.Pais;
import br.gov.es.siscap.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CidadeService {

    private final CidadeRepository repository;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<CidadeSelectDto> buscarSelect(FiltroCidadeEnum filtrarPor, Long id) {
        return switch (filtrarPor) {
            case PAIS -> repository.findAllByEstadoPais(new Pais(id)).stream().map(CidadeSelectDto::new).toList();
            case ESTADO -> repository.findAllByEstado(new Estado(id)).stream().map(CidadeSelectDto::new).toList();
        };
    }
}
