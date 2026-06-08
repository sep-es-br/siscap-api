package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.DesafiosPentahoBiDto;
import br.gov.es.siscap.dto.GestaoPentahoBiDto;
import br.gov.es.siscap.dto.IndicadorPentahoBiDto;
import br.gov.es.siscap.dto.indicadoresexternos.FiltroIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.FiltroLabelDto;
import br.gov.es.siscap.dto.indicadoresexternos.IndicadorDesafioExternoDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelDTO;
import br.gov.es.siscap.dto.indicadoresexternos.LabelValorDTO;
import br.gov.es.siscap.dto.indicadoresexternos.MetasIndicadorExternoDto;
import br.gov.es.siscap.dto.indicadoresexternos.OdsIndicadorExternoDto;
import br.gov.es.siscap.dto.indicadoresexternos.OdsPentahoBiDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.dto.indicadoresexternos.OrganizadorGestaoPentahoBiDto;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.IndicadorAvulsoForm;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
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

	@Value("${pentahoBI.siscap.gestao.target}")
	private String targetGestao;

	@Value("${pentahoBI.siscap.gestao.dataAccessId}")
	private String gestaoDataAccessId;

	@Value("${pentahoBI.siscap.desafios.target}")
	private String targetDesafios;

	@Value("${pentahoBI.siscap.desafios.dataAccessId}")
	private String desafiosDataAccessId;

	@Value("${pentahoBI.siscap.organizacao.target}")
	private String targetOrganizacao;

	@Value("${pentahoBI.siscap.organizacao.dataAccessId}")
	private String organizacaoDataAccessId;

	@Value("${pentahoBI.siscap.odsporindicadores.dataAccessId}")
	private String odsIndicadoresDataAccessId;

	@Value("${pentahoBI.siscap.odsporindicadores.target}")
	private String targetOdsIndicadores;

	private final ApiUtils apiUtils;
	
	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores() {

		List<GestaoPentahoBiDto> gestoes = this.listarGestoesAtivasBI();
		List<DesafiosPentahoBiDto> desafios = this.listarDesafiosGestaoBI();

		return gestoes.stream()
				.map(gestao -> {

					List<OrganizadorGestaoPentahoBiDto> organizadores = this
							.listarOrganizadoresGestaoBI(gestao.idGestao());

					List<LabelDTO> labels = montarLabelsGrupoSubgrupo(organizadores);

					List<IndicadorDesafioExternoDTO> desafiosDto = desafios.stream()
							.map(gd -> new IndicadorDesafioExternoDTO(
									gd.desafioId(),
									gd.desafio()))
							.distinct()
							.toList();

					return new OpcoesGestaoIndicadorDto(
							gestao.idGestao(),
							gestao.nomeGestao(),
							labels,
							desafiosDto,
							gestao.deAno() != null ? gestao.deAno() : 0,
							gestao.ateAno() != null ? gestao.ateAno() : 0);
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

		String idsIndicadores = listaIndicadoresBI.stream()
				.map(ind -> String.valueOf(ind.idIndicador()))
				.distinct()
				.collect(Collectors.joining(","));

		List<OdsIndicadorExternoDto> listaOdsBI = this.listarOdsPorIndicadoresBI(idsIndicadores);

		Map<Integer, List<OdsIndicadorExternoDto>> odsPorIndicador = listaOdsBI.stream()
				.collect(Collectors.groupingBy(
						OdsIndicadorExternoDto::IndicadorId,
						Collectors.mapping(ods -> new OdsIndicadorExternoDto(
								ods.IndicadorId(),
								ods.odsId(),
								ods.odsOrdem(),
								ods.odsNome(),
								ods.odsDescricao(),
								obterCorOds(ods.odsOrdem())), Collectors.toList())));

		return listaIndicadoresBI.stream()
				.collect(Collectors.groupingBy(IndicadorPentahoBiDto::idIndicador))
				.values()
				.stream()
				.map(grupo -> {

					IndicadorPentahoBiDto primeiro = grupo.get(0);

					Set<String> chavesMetas = new HashSet<>();

					List<MetasIndicadorExternoDto> metas = grupo.stream()
							.filter(item -> item.anoMeta() != null || item.valorMeta() != null)
							.map(item -> new MetasIndicadorExternoDto(
									null,
									item.anoMeta(),
									item.valorMeta() != null
											? toBigDecimal(item.valorMeta())
											: null))
							.filter(meta -> chavesMetas.add(meta.anoMeta() + "|" + meta.valorMeta()))
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

	private List<OdsIndicadorExternoDto> listarOdsPorIndicadoresBI(String idsIndicadores) {

		String parametroIndicadores = idsIndicadores != null && !idsIndicadores.trim().isEmpty()
				? idsIndicadores.trim()
				: "-1";

		Map<String, Object> params = Map.of(
				"paramp_ods", "-1",
				"paramp_indicadores", parametroIndicadores);

		return apiUtils.consult( targetOdsIndicadores, odsIndicadoresDataAccessId, siscapPath, params, rs -> {

			return new OdsIndicadorExternoDto(
					rs.get("IndicadorId").asInt(),
					rs.get("OdsId").asInt(),
					rs.get("ordemOds").asInt(),
					rs.get("nomeOds").asText(),
					rs.get("DescricaoOds").asText(),
					obterCorOds(rs.get("ordemOds").asInt()));
		});

	}

	public List<OpcoesIndicadoresDto> cadastrarIndicadorAvulso(IndicadorAvulsoForm form) {

		if (form == null) {
			throw new SiscapServiceException(Arrays.asList("Dados do indicador são obrigatórios"));
		}

		return Collections.emptyList();

	}

	public List<GestaoPentahoBiDto> listarGestoesAtivasBI() {

		String ativa = "1"; // gestoes ativas

		Map<String, Object> params = Map.of(
				"paramp_ativa", ativa);

		String pmoPath = siscapPath;
		String target = targetGestao;
		String dataAccessId = gestaoDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new GestaoPentahoBiDto(
						rs.get("idGestao").asInt(),
						rs.get("ativa").asInt(),
						rs.get("nomeGestao").asText(),
						rs.get("descricaoGestao").asText(),
						rs.get("deAno").asInt(),
						rs.get("ateAno").asInt(),
						rs.get("modelNameGestao").asText()));

	}

	public List<DesafiosPentahoBiDto> listarDesafiosGestaoBI() {

		String desafios = "-1"; // todos

		Map<String, Object> params = Map.of(
				"paramp_desafio", desafios);

		String pmoPath = siscapPath;
		String target = targetDesafios;
		String dataAccessId = desafiosDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new DesafiosPentahoBiDto(
						rs.get("gestaoId").asInt(),
						rs.get("desafioId").asInt(),
						rs.get("desafio").asText()));

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

		Map<String, Object> params = Map.of();

		return apiUtils.consult(targetOds, odsDataAccessId, siscapPath, params,
				rs -> new OdsPentahoBiDto(
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

	public List<LabelDTO> montarLabelsGrupoSubgrupo(List<OrganizadorGestaoPentahoBiDto> listaBi) {
		
		Map<String, Map<Integer, LabelValorDTO>> labelsMap = new LinkedHashMap<>();

		for (OrganizadorGestaoPentahoBiDto item : listaBi) {

			adicionarValorLabel(
				labelsMap,
				item.labelGrupo(),
				item.idGrupo(),
				item.valorGrupo(), 
				null);

			adicionarValorLabel(
				labelsMap,
				item.labelSubGrupo(),
				item.idSubGrupo(),
				item.valorSubGrupo(),
				item.idGrupo());
		
		}

		return labelsMap.entrySet().stream()
			.map(entry -> new LabelDTO(
					(Integer) entry.getKey().hashCode(),
					entry.getKey(),
					null,
					new ArrayList<>(entry.getValue().values())))
			.toList();
		
	}

	private void adicionarValorLabel(
			Map<String, Map<Integer, LabelValorDTO>> labelsMap,
			String nomeLabel,
			Integer idValor,
			String valorLabel,
			Integer idPai) {

		if (nomeLabel == null || idValor == null || valorLabel == null) {
			return;
		}

		labelsMap
				.computeIfAbsent(nomeLabel.trim(), key -> new LinkedHashMap<>())
				.putIfAbsent(
						idValor,
						new LabelValorDTO(
								idValor,
								valorLabel.trim(),
								idPai));
	}

	private List<OrganizadorGestaoPentahoBiDto> listarOrganizadoresGestaoBI(Integer idGestao) {

		Map<String, Object> params = Map.of(
				"paramp_idGestao", idGestao != null ? String.valueOf(idGestao) : "-1");

		return apiUtils.consult(targetOrganizacao, organizacaoDataAccessId, siscapPath, params,
				rs -> new OrganizadorGestaoPentahoBiDto(
						rs.get("gestaoId").asInt(),
						rs.get("idGrupo").asInt(),
						rs.get("labelGrupo").asText(),
						rs.get("descricaoGrupo").asText(),
						rs.get("idSubGrupo").asInt(),
						rs.get("labelSubGrupo").asText(),
						rs.get("descricaoSubGrupo").asText()));

	}

	// public List<OdsPentahoBiDto> listarOds() {
	// return null;
	// }

}