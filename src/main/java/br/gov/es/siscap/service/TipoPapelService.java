package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.repository.TipoPapelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoPapelService {

	private final TipoPapelRepository repository;

	public List<OpcoesDto> listarOpcoesDropdown() {
		return repository.findAll().stream().filter(papel -> papel.getId() != 2).map(OpcoesDto::new).toList();
	}
}