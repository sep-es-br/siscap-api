package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.indicadoresexternos.DesafioDTO;
import br.gov.es.siscap.dto.indicadoresexternos.IndicadorDesafioExternoDTO;
import br.gov.es.siscap.dto.indicadoresexternos.IndicadorGestaoResumoDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelValorDTO;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.models.IndicadorExterno;
import br.gov.es.siscap.models.IndicadorFatoExterno;
import br.gov.es.siscap.models.IndicadorGestaoExterno;
import br.gov.es.siscap.models.IndicadorGestaoLabel;
import br.gov.es.siscap.repository.IndicadorExternoRepository;
import br.gov.es.siscap.repository.IndicadorGestaoExternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorExternoService {

	private final IndicadorGestaoExternoRepository repository;
	private final IndicadorExternoRepository indicadorExternoRepository;

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

					List<IndicadorDesafioExternoDTO> desafios = gestao.getDesafios()
							.stream().map(gd -> {
								return new IndicadorDesafioExternoDTO(gd.getId(), gd.getNome());
							}).toList();

					return new OpcoesGestaoIndicadorDto(
							gestao.getId(),
							gestao.getNome(),
							labels,
							desafios);

				})
				.toList();

	}

	public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(
			Long filtroGestao,
			List<Long> filtroLabel,
			List<Long> filtroLabelValor,
			List<Long> filtroDesafio) {

		if (filtroGestao == null) {
			throw new SiscapServiceException(Arrays.asList("Gestão é obrigatória"));
		}

		List<Long> labels = (filtroLabel == null || filtroLabel.isEmpty()) ? null : filtroLabel;
		List<Long> valores = (filtroLabelValor == null || filtroLabelValor.isEmpty()) ? null : filtroLabelValor;
		List<Long> desafios = (filtroDesafio == null || filtroDesafio.isEmpty()) ? null : filtroDesafio;

		List<IndicadorExterno> indicadores = indicadorExternoRepository.buscarPorFiltros(
			filtroGestao, labels, desafios);

		return indicadores.stream()
				.map(this::toDto)
				.toList();

	}

	private OpcoesIndicadoresDto toDto(IndicadorExterno ie) {

		return new OpcoesIndicadoresDto(

				ie.getId(),
				ie.getNome(),
				ie.getUnidadeMedida(),
				ie.getPolaridade(),
				ie.getMedidoPor());

				// // GESTÃO
				// ie.getGestao() != null
				// 		? new IndicadorGestaoResumoDTO(
				// 				ie.getGestao().getId(),
				// 				ie.getGestao().getNome(),
				// 				ie.getGestao().getAtiva())
				// 		: null,

				// // DESAFIOS
				// ie.getDesafio() != null
				// 		? new DesafioDTO(
				// 				ie.getDesafio().getId(),
				// 				ie.getDesafio().getNome())
				// 		: null,

				// // LABELS
				// ie.getGestao().getLabels() != null
				// 		? ie.getGestao().getLabels().stream()
				// 				.map(igl -> new LabelDTO(
				// 						igl.getLabel().getId(),
				// 						igl.getLabel().getNome(),
				// 						0,
				// 						igl.getLabel().getValores() != null
				// 								? igl.getLabel().getValores().stream()
				// 										.map(v -> new LabelValorDTO(
				// 												v.getId(),
				// 												v.getValor()))
				// 										.distinct() // evita duplicidade por causa do join fetch
				// 										.toList()
				// 								: List.of()
				// 					))
				// 				.toList()
				// 		: List.of());

	}

}