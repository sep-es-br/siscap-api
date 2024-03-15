package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EixoSelectDto;
import br.gov.es.siscap.models.Plano;
import br.gov.es.siscap.repository.EixoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EixoService {

    private final EixoRepository repository;

    public List<EixoSelectDto> buscarSelectPorPlano(Long idPlano) {
        return repository.findAllByPlano(new Plano(idPlano)).stream().map(EixoSelectDto::new).toList();
    }

}