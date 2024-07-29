package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.repository.PapelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PapelService {

	private final PapelRepository repository;

	public List<SelectDto> buscarSelect() {
		return repository.findAll().stream().filter(papel -> papel.getId() != 2).map(SelectDto::new).toList();
	}
}
