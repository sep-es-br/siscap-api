package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.repository.ProgramaRepository;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjetoService {

	private final ProjetoRepository projetoRepository;
	private final ProgramaRepository programaRepository;

	public List<ObjetoOpcoesDto> listarOpcoesDropdown() {

		List<ObjetoOpcoesDto> objetoOpcoesDtoList = new ArrayList<>();

		List<ObjetoOpcoesDto> objetoOpcoesDtoProgramaMapList = programaRepository
					.findAll()
					.stream()
					.map(ObjetoOpcoesDto::new)
					.toList();

		List<ObjetoOpcoesDto> objetoOpcoesDtoProjetoMapList = projetoRepository
					.findAll()
					.stream()
					.map(ObjetoOpcoesDto::new)
					.toList();

		objetoOpcoesDtoList.addAll(objetoOpcoesDtoProgramaMapList);
		objetoOpcoesDtoList.addAll(objetoOpcoesDtoProjetoMapList);

		return objetoOpcoesDtoList;
	}
}