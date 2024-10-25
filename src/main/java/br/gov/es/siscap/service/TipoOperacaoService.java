package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.repository.TipoOperacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoOperacaoService {

	private final TipoOperacaoRepository repository;

	public List<SelectDto> buscarSelect() {
		return repository.findAll().stream().map(SelectDto::new).toList();
	}
}
