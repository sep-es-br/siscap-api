package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.MicrorregiaoCidadesDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.models.Cidade;
import br.gov.es.siscap.models.Estado;
import br.gov.es.siscap.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicrorregiaoCidadesService {

	private static final Long idEstadoEspiritoSanto = 18L;
	private final MicrorregiaoService microrregiaoService;
	private final CidadeRepository cidadeRepository;

	public List<MicrorregiaoCidadesDto> buscarSelect() {

		List<MicrorregiaoCidadesDto> microrregiaoCidadesDtoList = new ArrayList<>();

		List<SelectDto> microrregiaoSelectDtoList = microrregiaoService.buscarSelect();

		List<Cidade> cidadeList = cidadeRepository.findAllByEstadoOrderByNome(new Estado(idEstadoEspiritoSanto));

		for (SelectDto microrregiaoSelectDto : microrregiaoSelectDtoList) {
			MicrorregiaoCidadesDto microrregiaoCidadesDto = new MicrorregiaoCidadesDto(microrregiaoSelectDto);
			microrregiaoCidadesDto.setCidades(filtrarCidadePorMicrorregiao(cidadeList, microrregiaoSelectDto.id()));
			microrregiaoCidadesDtoList.add(microrregiaoCidadesDto);
		}

		return microrregiaoCidadesDtoList;
	}


	private List<SelectDto> filtrarCidadePorMicrorregiao(List<Cidade> cidadeList, Long idMicrorregiao) {
		return cidadeList.stream().filter(cidade -> cidade.getMicrorregiao().getId().equals(idMicrorregiao)).map(SelectDto::new).toList();
	}
}
