package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ObjetoSelectDto;
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

	public List<ObjetoSelectDto> buscarSelect() {

		List<ObjetoSelectDto> objetoSelectDtoList = new ArrayList<>();

		List<ObjetoSelectDto> objetoSelectDtoProjetoMapList = projetoRepository
					.findAll()
					.stream()
					.map(ObjetoSelectDto::new)
					.toList();

		List<ObjetoSelectDto> objetoSelectDtoProgramaMapList = programaRepository
					.findAll()
					.stream()
					.map(ObjetoSelectDto::new)
					.toList();

		objetoSelectDtoList.addAll(objetoSelectDtoProjetoMapList);
		objetoSelectDtoList.addAll(objetoSelectDtoProgramaMapList);

		return objetoSelectDtoList;
	}
}
