package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.indicadoresexternos.LabelDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelValorDTO;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.models.IndicadorGestaoExterno;
import br.gov.es.siscap.models.IndicadorGestaoLabel;
import br.gov.es.siscap.repository.IndicadorGestaoExternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorExternoService {

	private final IndicadorGestaoExternoRepository repository;

	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores() {

		List<IndicadorGestaoExterno> gestoes = repository.findAllAtivasComLabels();

		return gestoes.stream()
				.map(gestao -> {

					List<LabelDTO> labels = gestao.getLabels().stream()
							.sorted(Comparator.comparing(IndicadorGestaoLabel::getOrdem))
							.map(gl -> {

								var label = gl.getLabel();

								List<LabelValorDTO> valores = label.getValores() != null
										? label.getValores().stream()
												.map(v -> new LabelValorDTO(
														v.getId(),
														v.getValor()))
												.distinct() // evita duplicidade por causa do join fetch
												.toList()
										: List.of();

								return new LabelDTO(
										label.getId(),
										label.getNome(),
										gl.getOrdem(),
										valores);
							})
							.toList();

					return new OpcoesGestaoIndicadorDto(
							gestao.getId(),
							gestao.getNome(),
							labels);

				})
				.toList();

	}

}