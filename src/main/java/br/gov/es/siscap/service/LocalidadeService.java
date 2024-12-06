package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.LocalidadeOpcoesDto;
import br.gov.es.siscap.repository.LocalidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalidadeService {

	private final LocalidadeRepository repository;

	public List<LocalidadeOpcoesDto> listarOpcoesDropdown() {
		return repository.findAll()
					.stream()
					.map(LocalidadeOpcoesDto::new)
					.toList();
	}
}