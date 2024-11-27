package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.InteressadoOpcoesDto;
import br.gov.es.siscap.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteressadoService {

	private final PessoaRepository pessoaRepository;

	public List<InteressadoOpcoesDto> listarOpcoesDropdown() {
		return pessoaRepository.findAll().stream().map(InteressadoOpcoesDto::new).toList();
	}
}