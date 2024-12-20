package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.models.Eixo;
import br.gov.es.siscap.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaService {

	private final AreaRepository repository;

	public boolean existePorId(Long id) {
		return repository.existsById(id);
	}

	public List<OpcoesDto> listarOpcoesDropdown(Long idEixo) {
		return repository.findAllByEixoOrderByNome(new Eixo(idEixo)).stream().map(OpcoesDto::new).toList();
	}
}