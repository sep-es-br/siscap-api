package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.enums.FiltroCidade;
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

    public List<SelectDto> buscarSelect(FiltroCidade filtrarPor, Long id) {
        return switch (filtrarPor) {
            case PAIS -> repository.findAllByEstadoPaisOrderByNome(new Pais(id)).stream().map(SelectDto::new).toList();
            case ESTADO -> repository.findAllByEstadoOrderByNome(new Estado(id)).stream().map(SelectDto::new).toList();
        };
    }
}
