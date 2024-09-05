package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.repository.ValorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValorService {

	private final ValorRepository repository;

	public List<SelectDto> buscarSelect() {
		return repository.findAll().stream().map(SelectDto::new).toList();
	}
}
