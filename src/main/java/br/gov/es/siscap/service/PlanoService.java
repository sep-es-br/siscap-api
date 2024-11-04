package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanoService {

	public final PlanoRepository repository;

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(OpcoesDto::new).toList();
	}
}