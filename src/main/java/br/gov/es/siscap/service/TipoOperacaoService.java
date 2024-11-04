package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.repository.TipoOperacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoOperacaoService {

	private final TipoOperacaoRepository repository;

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().map(OpcoesDto::new).toList();
	}
}