package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.models.Pais;
import br.gov.es.siscap.repository.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoService {

    private final EstadoRepository repository;

    public List<SelectDto> buscarSelect(Long idPais) {
        return repository.findAllByPaisOrderByNome(new Pais(idPais)).stream().map(SelectDto::new).toList();
    }
}
