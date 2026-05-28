package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.IndicadorPentahoBiDto;
import br.gov.es.siscap.dto.OdsPentahoBiDto;
import br.gov.es.siscap.dto.indicadoresexternos.FiltroIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.FiltroLabelDto;
import br.gov.es.siscap.dto.indicadoresexternos.IndicadorDesafioExternoDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelValorDTO;
import br.gov.es.siscap.dto.indicadoresexternos.MetasIndicadorExternoDto;
import br.gov.es.siscap.dto.indicadoresexternos.OdsIndicadorExternoDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.IndicadorAvulsoForm;
import br.gov.es.siscap.models.IndicadorGestaoExterno;
import br.gov.es.siscap.models.IndicadorGestaoLabel;
import br.gov.es.siscap.repository.IndicadorGestaoExternoRepository;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorExternoService {

	@Value("${pentahoBI.baseURL}")
	private String pentahoBaseUrl;

	@Value("${pentahoBI.userId}")
	private String pentahoUserId;

	@Value("${pentahoBI.password}")
	private String pentahoPassword;

	@Value("${pentahoBI.siscap.path}")
	private String siscapPath;

	@Value("${pentahoBI.siscap.indicadores.dataAccessId}")
	private String indicadoresDataAccessId;

	@Value("${pentahoBI.siscap.indicadores.target}")
	private String targetIndicadores;

	@Value("${pentahoBI.siscap.ods.dataAccessId}")
	private String odsDataAccessId;

	@Value("${pentahoBI.siscap.ods.target}")
	private String targetOds;

	private final IndicadorGestaoExternoRepository repository;
	private final ApiUtils apiUtils;

	private final Logger logger = LogManager.getLogger(IndicadorExternoService.class);

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
							desafios,
							gestao.getDoAno() != null ? gestao.getDoAno() : 0,
							gestao.getAteAno() != null ? gestao.getAteAno() : 0);

				})
				.toList();

	}

	public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(
			Long filtroGestao,
			FiltroIndicadorDto filtro) {

		if (filtroGestao == null) {
			throw new SiscapServiceException(Arrays.asList("Gestão é obrigatória"));
		}

		List<IndicadorPentahoBiDto> listaIndicadoresBI = this.listarIndicadoresBI(filtro);

		List<OdsPentahoBiDto> listaOdsBI = this.listarOdsBI();

		Map<Integer, List<OdsIndicadorExternoDto>> odsPorIndicador = listaOdsBI.stream()
				.collect(Collectors.groupingBy(
						OdsPentahoBiDto::indicadorId,
						Collectors.mapping(ods -> new OdsIndicadorExternoDto(
								null, // idOdsIndicadorExterno
								null, // idOdsExterno
								ods.odsId(),
								ods.ordemOds(),
								ods.nomeOds(),
								ods.descricaoOds(),
								obterCorOds(ods.ordemOds())), Collectors.toList())));

		return listaIndicadoresBI.stream()
				.collect(Collectors.groupingBy(IndicadorPentahoBiDto::idIndicador))
				.values()
				.stream()
				.map(grupo -> {

					IndicadorPentahoBiDto primeiro = grupo.get(0);

					List<MetasIndicadorExternoDto> metas = grupo.stream()
							.filter(item -> item.anoMeta() != null || item.valorMeta() != null)
							.map(item -> new MetasIndicadorExternoDto(
									null,
									item.anoMeta(),
									item.valorMeta() != null
											? toBigDecimal(item.valorMeta())
											: null))
							.toList();

					List<OdsIndicadorExternoDto> ods = odsPorIndicador.getOrDefault(
							primeiro.idIndicador(),
							Collections.emptyList());

					return new OpcoesIndicadoresDto(
							primeiro.idIndicador(),
							primeiro.nomeIndicador(),
							primeiro.unidadeMedida(),
							primeiro.polaridade(),
							primeiro.medidoPor(),
							metas,
							primeiro.maiorAnoIndicador(),
							primeiro.maiorMetaIndicador() != null
									? BigDecimal.valueOf(primeiro.maiorMetaIndicador())
									: null,
							ods);
				})
				.toList();

	}

	public List<OpcoesIndicadoresDto> cadastrarIndicadorAvulso(IndicadorAvulsoForm form) {

		if (form == null) {
			throw new SiscapServiceException(Arrays.asList("Dados do indicador são obrigatórios"));
		}

		return Collections.emptyList();

	}

	public List<IndicadorPentahoBiDto> listarIndicadoresBI(FiltroIndicadorDto filtro) {

		String desafios = "-1"; // todos
		String organizadores = "-1"; // todos

		if (filtro != null) {

			if (filtro.desafios() != null && !filtro.desafios().isEmpty()) {
				desafios = montarListaIds(filtro.desafios());
			}

			if (filtro.labels() != null && !filtro.labels().isEmpty()) {
				organizadores = montarOrganizadores(filtro.labels());
			}
		}

		Map<String, Object> params = Map.of(
				"paramp_desafio", desafios,
				"paramp_organizador", organizadores);

		String pmoPath = siscapPath;
		String target = targetIndicadores;
		String dataAccessId = indicadoresDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new IndicadorPentahoBiDto(
						rs.get("ativa").asInt(),
						rs.get("idGestao").asInt(),
						rs.get("nomeGestao").asText(),
						rs.get("modelNameGestao").asText(),
						rs.get("idDesafio").asInt(),
						rs.get("nomeDesafio").asText(),
						rs.get("idOrganizador").asInt(),
						rs.get("nomeOrganizador").asText(),
						rs.get("modelNameOrganizador").asText(),
						rs.get("idIndicador").asInt(),
						rs.get("nomeIndicador").asText(),
						rs.get("unidadeMedida").asText(),
						rs.get("polaridade").asText(),
						rs.get("medidoPor").asText(),
						rs.get("anoMeta").asInt(),
						rs.get("valorMeta").asText(),
						rs.get("maiorAnoIndicador").asInt(),
						rs.get("maiorMetaIndicador").asDouble()));

	}

	private String montarOrganizadores(List<FiltroLabelDto> labels) {

		String organizadores = labels == null
				? "-1"
				: labels.stream()
						.filter(label -> label.idLabelValores() != null)
						.flatMap(label -> label.idLabelValores().stream())
						.distinct()
						.map(String::valueOf)
						.collect(Collectors.joining(","));

		if (organizadores.isBlank()) {
			organizadores = "-1";
		}

		return organizadores;

	}

	private String montarListaIds(List<Long> ids) {

		if (ids == null || ids.isEmpty()) {
			return "-1";
		}

		return ids.stream()
				.distinct()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}

	public List<OdsPentahoBiDto> listarOdsBI() {

		Map<String, Object> params = Map.of(
				"paramp_ods", "-1");

		return apiUtils.consult(targetOds, odsDataAccessId, siscapPath, params,
				rs -> new OdsPentahoBiDto(
						rs.get("IndicadorId").asInt(),
						rs.get("OdsId").asInt(),
						rs.get("DescricaoOds").asText(),
						rs.get("nomeOds").asText(),
						rs.get("ordemOds").asInt(),
						obterCorOds(rs.get("ordemOds").asInt())));

	}

	private BigDecimal toBigDecimal(String valor) {

		if (valor == null || valor.isBlank()) {
			return null;
		}

		valor = valor.trim();

		if (valor.equalsIgnoreCase("null")
				|| valor.equalsIgnoreCase("nan")
				|| valor.equalsIgnoreCase("n/d")) {
			return null;
		}

		return new BigDecimal(valor.replace(",", "."));
	}

	private String obterCorOds(Integer ordemOds) {

		if (ordemOds == null) {
			return null;
		}

		return switch (ordemOds) {
			case 1 -> "#E5243B";
			case 2 -> "#DDA63A";
			case 3 -> "#4C9F38";
			case 4 -> "#C5192D";
			case 5 -> "#FF3A21";
			case 6 -> "#26BDE2";
			case 7 -> "#FCC30B";
			case 8 -> "#A21942";
			case 9 -> "#FD6925";
			case 10 -> "#DD1367";
			case 11 -> "#FD9D24";
			case 12 -> "#BF8B2E";
			case 13 -> "#3F7E44";
			case 14 -> "#0A97D9";
			case 15 -> "#56C02B";
			case 16 -> "#00689D";
			case 17 -> "#19486A";
			case 18 -> "#7A3A1A";
			default -> null;
		};
	}

	// private boolean aplicarFiltroIndicador(
	// 		IndicadorPentahoBiDto item,
	// 		Long filtroGestao,
	// 		FiltroIndicadorDto filtro) {
	// 	if (item.idGestao() == null || !Objects.equals(item.idGestao().longValue(), filtroGestao)) {
	// 		return false;
	// 	}
	// 	if (filtro == null) {
	// 		return true;
	// 	}
	// 	if (filtro.desafios() != null && !filtro.desafios().isEmpty()) {
	// 		return (item.idDesafio() == null || !filtro.desafios().contains(item.idDesafio().longValue()));
	// 	}
	// 	if (filtro.labels() != null && !filtro.labels().isEmpty()) {
	// 		for (FiltroLabelDto label : filtro.labels()) {
	// 			if (label.idLabel() == null
	// 					|| label.idLabelValores() == null
	// 					|| label.idLabelValores().isEmpty()) {
	// 				continue;
	// 			}
	// 			boolean match = item.idOrganizador() != null
	// 					// && item. idLabelValor() != null
	// 					&& Objects.equals(item.idOrganizador().longValue(), label.idLabel());
	// 			// && label.idLabelValores().contains(item.idLabelValor().longValue());
	// 			if (!match) {
	// 				return false;
	// 			}
	// 		}
	// 	}
	// 	return true;
	// }

}