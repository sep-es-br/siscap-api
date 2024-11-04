package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
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

	public List<OpcoesDto> listarOpcoesDropdownPorPlano(Long idPlano) {
		return repository.findAllByPlanoOrderByNome(new Plano(idPlano)).stream().map(OpcoesDto::new).toList();
	}
}