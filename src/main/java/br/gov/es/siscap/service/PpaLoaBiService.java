package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.AcaoPpaLoaDto;
import br.gov.es.siscap.dto.ChaveAcaoLoa;
import br.gov.es.siscap.dto.DetalhamentoOrcamentarioLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPeriodoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPpaLoaDto;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PpaLoaBiService {

	@Value("${pentahoBI.baseURL}")
	private String pentahoBaseUrl;

	@Value("${pentahoBI.userId}")
	private String pentahoUserId;

	@Value("${pentahoBI.password}")
	private String pentahoPassword;

	@Value("${pentahoBI.sigefes.path}")
	private String siscapSigefesPath;

	@Value("${pentahoBI.sigefes.planejamento.dataAccessId}")
	private String planejamentoDataAccessId;

	@Value("${pentahoBI.sigefes.planejamento.target}")
	private String targetPlanejamentoPpa;

	@Value("${pentahoBI.sigefes.anosppa.dataAccessId}")
	private String anosPpaDataAccessId;

	@Value("${pentahoBI.sigefes.anosppa.target}")
	private String targetAnosPpa;

	@Value("${pentahoBI.sigefes.uosano.dataAccessId}")
	private String uosPpaDataAccessId;

	@Value("${pentahoBI.sigefes.uosano.target}")
	private String targetUosPpa;

	@Value("${pentahoBI.sigefes.funcoes.dataAccessId}")
	private String funcoesPpaDataAccessId;

	@Value("${pentahoBI.sigefes.funcoes.target}")
	private String targetFuncoesPpa;

	@Value("${pentahoBI.sigefes.programas.dataAccessId}")
	private String programasPpaDataAccessId;

	@Value("${pentahoBI.sigefes.programas.target}")
	private String targetProgramasPpa;

	@Value("${pentahoBI.sigefes.acoes.dataAccessId}")
	private String acoesPpaDataAccessId;

	@Value("${pentahoBI.sigefes.acoes.target}")
	private String targetAcoesPpa;

	@Value("${pentahoBI.sigefes.dadosacoes.target}")
	private String targetDadosAcoesPpa;

	@Value("${pentahoBI.sigefes.dadosacoes.dataAccessId}")
	private String dadosAcoesPpaDataAccessId;

	@Value("${pentahoBI.sigefes.dadosloa.target}")
	private String targetDadosLoa;

	@Value("${pentahoBI.sigefes.dadosloa.dataAccessId}")
	private String dadosLoaDataAccessId;

	private final ApiUtils apiUtils;

	private final Logger logger = LogManager.getLogger(PpaLoaBiService.class);

	public OpcoesPeriodoPpaLoaDto listarPeriodoPpaAtivo() {

		Map<String, Object> params = Map.of();

		String pmoPath = siscapSigefesPath;
		String target = targetPlanejamentoPpa;
		String dataAccessId = planejamentoDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPeriodoPpaLoaDto(null, rs.get("ppa").asText())).get(0);

	}

	public List<OpcoesPpaLoaDto> listarAnosPpaAtivo() {

		Map<String, Object> params = Map.of();

		String pmoPath = siscapSigefesPath;
		String target = targetAnosPpa;
		String dataAccessId = anosPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("ano").asLong(),
						rs.get("ano").asText()));

	}

	public List<OpcoesPpaLoaDto> listarUosAnoPpa(String ppa) {

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa);

		String pmoPath = siscapSigefesPath;
		String target = targetUosPpa;
		String dataAccessId = uosPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_uo").asLong(),
						rs.get("cod_uo").asText() + " - " + rs.get("mne_uo").asText()));

	}

	public List<OpcoesPpaLoaDto> listarFuncoes(String ppa, List<Long> uos) {

		String uosFormatados = (uos == null || uos.isEmpty())
				? "-1"
				: uos.stream()
						.distinct()
						.map(uo -> String.format("%05d", uo))
						.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa,
				"paramp_cod_uo", uosFormatados);

		String pmoPath = siscapSigefesPath;
		String target = targetFuncoesPpa;
		String dataAccessId = funcoesPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_funcao").asLong(),
						rs.get("cod_funcao").asText() + " - " + rs.get("nom_funcao").asText()));

	}

	public List<OpcoesPpaLoaDto> listarProgramas(String ppa, List<Long> uos, List<Long> funcoes) {

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.distinct()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa,
				"paramp_cod_uo", uosFormatados,
				"paramp_cod_funcao", funcoesFormatadas);

		String pmoPath = siscapSigefesPath;
		String target = targetProgramasPpa;
		String dataAccessId = programasPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_programa").asLong(),
						rs.get("cod_programa").asText() + " - " + rs.get("nom_programa").asText()));

	}

	public List<OpcoesPpaLoaDto> listarAcoes(List<Long> funcoes, List<Long> programas, String ppa,
			List<Long> uos) {

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = (funcoes == null || funcoes.isEmpty())
				? "-1"
				: funcoes.stream()
						.distinct()
						.map(funcao -> String.format("%02d", funcao))
						.collect(Collectors.joining(","));

		String programasFormatados = (programas == null || programas.isEmpty())
				? "-1"
				: programas.stream()
						.distinct()
						.map(programa -> String.format("%04d", programa))
						.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa,
				"paramp_cod_uo", uosFormatados,
				"paramp_cod_funcao", funcoesFormatadas,
				"paramp_cod_programa", programasFormatados);

		String pmoPath = siscapSigefesPath;
		String target = targetAcoesPpa;
		String dataAccessId = acoesPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_acao").asLong(),
						rs.get("cod_acao").asText() + " - " + rs.get("nom_acao").asText()));

	}

	// public List<AcaoPpaLoaDto> dadosAcoes(String ppa, List<Long> funcoes,
	// List<Long> programas, List<Long> anos,
	// List<Long> uos,
	// List<Long> acoes) {

	// String anosFormatados = anos.stream()
	// .distinct()
	// .map(String::valueOf)
	// .collect(Collectors.joining(","));

	// String uosFormatados = uos.stream()
	// .distinct()
	// .map(uo -> String.format("%05d", uo))
	// .collect(Collectors.joining(","));

	// String funcoesFormatadas = (funcoes == null || funcoes.isEmpty())
	// ? "-1"
	// : funcoes.stream()
	// .distinct()
	// .map(funcao -> String.format("%02d", funcao))
	// .collect(Collectors.joining(","));

	// String programasFormatados = (programas == null || programas.isEmpty())
	// ? "-1"
	// : programas.stream()
	// .distinct()
	// .map(programa -> String.format("%04d", programa))
	// .collect(Collectors.joining(","));

	// String acoesFormatadas = (acoes == null || acoes.isEmpty())
	// ? "-1"
	// : acoes.stream()
	// .distinct()
	// .map(acao -> String.format("%04d", acao))
	// .collect(Collectors.joining(","));

	// Map<String, Object> params = Map.of(
	// "paramp_ppa", ppa,
	// "paramp_ano", anosFormatados,
	// "paramp_cod_uo", uosFormatados,
	// "paramp_cod_programa", programasFormatados,
	// "paramp_cod_acao", acoesFormatadas,
	// "paramp_cod_funcao", funcoesFormatadas);

	// String pmoPath = siscapSigefesPath;
	// String target = targetDadosAcoesPpa;
	// String dataAccessId = dadosAcoesPpaDataAccessId;

	// List<AcaoPpaLoaDto> dadosAcoes = apiUtils.consult(target, dataAccessId,
	// pmoPath, params,
	// rs -> new AcaoPpaLoaDto(

	// rs.get("cod_acao").asLong(), // id

	// rs.get("cod_orgao").asText(null),
	// rs.get("sigla").asText(null),
	// rs.get("nom_orgao").asText(null),

	// rs.get("cod_uo").asText(null),
	// rs.get("mne_uo").asText(null),
	// rs.get("nom_uo").asText(null),

	// rs.get("cod_programa").asText(null),
	// rs.get("nom_programa").asText(null),

	// rs.get("cod_acao").asText(null),
	// rs.get("nom_acao").asText(null),

	// rs.get("cod_funcao").asText(null),
	// rs.get("nom_funcao").asText(null),

	// rs.get("vlr_ppa").decimalValue(),

	// BigDecimal.ZERO,

	// String.valueOf(anos.get(0)), // rs.get("ano_acao") == null ? "" :
	// rs.get("ano_acao").asText(null),

	// List.of() // detalhamentoOrcamentarioLoa

	// ));

	// if (dadosAcoes.isEmpty()) {
	// throw new ValidacaoSiscapException(Arrays.asList("Nenhum dado encontrado para
	// os filtros informados."));
	// }

	// Map<String, Object> paramsLoa = Map.of(
	// "paramp_ano", anosFormatados,
	// "paramp_cod_uo", uosFormatados,
	// "paramp_cod_programa", programasFormatados,
	// "paramp_cod_acao", acoesFormatadas,
	// "paramp_cod_funcao", funcoesFormatadas);

	// String targetLoa = targetDadosLoa;
	// String dataAccessIdLoa = dadosLoaDataAccessId;

	// List<DadosLoaBiDto> dadosLoa = apiUtils.consult(targetLoa, dataAccessIdLoa,
	// pmoPath, paramsLoa,
	// rs -> {

	// return new DadosLoaBiDto(
	// rs.get("orgao").asText(null),
	// rs.get("sigla").asText(null),
	// rs.get("nom_orgao").asText(null),
	// rs.get("uo").asText(null),
	// rs.get("mne_uo").asText(null),
	// rs.get("NOM_UO").asText(null),
	// rs.get("COD_PROGRAMA").asText(null),
	// rs.get("NOM_PROGRAMA").asText(null),
	// rs.get("acao").asText(null),
	// rs.get("NOM_ACAO").asText(null),

	// // algumas acoes apesar de estarem no ppa nao possuem loa entao esses valores
	// // vem vazios
	// rs.get("grupo").asText(""),
	// rs.get("NOM_GRUPO_DESPESA").asText(""),
	// rs.get("SGL_GRUPO_DESPESA").asText(""),
	// rs.get("modalidade").asText(""),
	// rs.get("iduso").asText(""),
	// rs.get("fonte").asText(""),
	// rs.get("loa_fin").decimalValue(),

	// rs.get("nome_modalidade").asText(""),
	// rs.get("nome_iduso").asText(""),
	// rs.get("nome_fonte").asText("")

	// );
	// }

	// );

	// if (dadosLoa.isEmpty()) {
	// dadosLoa = Arrays.asList(new DadosLoaBiDto(
	// null,
	// null,
	// null,
	// null,
	// null,
	// null,
	// null,
	// null,
	// null,
	// null,
	// "",
	// "",
	// "",
	// "",
	// "",
	// "",
	// BigDecimal.ZERO,
	// "",
	// "",
	// ""));
	// }
	// Map<ChaveAcaoLoa, List<DetalhamentoOrcamentarioLoaDto>> detalhamentosPorAcao
	// = dadosLoa.stream()
	// .collect(Collectors.groupingBy(
	// item -> new ChaveAcaoLoa(
	// normalizarCodigo(item.unidadeOrcamentaria()),
	// normalizarCodigo(item.codigoPrograma()),
	// normalizarCodigo(item.codigoAcao())),
	// Collectors.mapping(
	// item -> new DetalhamentoOrcamentarioLoaDto(
	// item.codigoGrupoDespesa(),
	// item.codigoModalidade(),
	// item.idUso(),
	// item.fonte(),
	// item.valorLoa(),
	// item.nomeGrupoDespesa(),
	// item.nomeModalidade(),
	// item.nomeIdUso(),
	// item.nomeFonte()),
	// Collectors.toList())));
	// if (detalhamentosPorAcao.isEmpty()) {
	// throw new ValidacaoSiscapException(
	// Arrays.asList("Nenhum detalhamento orçamentário encontrado para os filtros
	// informados."));
	// }
	// dadosAcoes = dadosAcoes.stream()
	// .map(acao -> {
	// ChaveAcaoLoa chave = new ChaveAcaoLoa(
	// normalizarCodigo(acao.codigoUnidadeOrcamentaria()),
	// normalizarCodigo(acao.codigoPrograma()),
	// normalizarCodigo(acao.codigoAcao()));
	// List<DetalhamentoOrcamentarioLoaDto> detalhamentosDaAcao =
	// detalhamentosPorAcao.getOrDefault(chave,
	// List.of());
	// BigDecimal valorTotalLoa = Optional
	// .ofNullable(detalhamentosDaAcao)
	// .orElseGet(List::of)
	// .stream()
	// .filter(Objects::nonNull) // remove DTOs nulos
	// .map(DetalhamentoOrcamentarioLoaDto::valor)
	// .filter(Objects::nonNull) // remove valores nulos
	// .reduce(BigDecimal.ZERO, BigDecimal::add);
	// return new AcaoPpaLoaDto(
	// acao.id(),
	// acao.codigoOrgao(),
	// acao.siglaOrgao(),
	// acao.nomeOrgao(),
	// acao.codigoUnidadeOrcamentaria(),
	// acao.siglaUnidadeOrcamentaria(),
	// acao.nomeUnidadeOrcamentaria(),
	// acao.codigoPrograma(),
	// acao.nomePrograma(),
	// acao.codigoAcao(),
	// acao.nomeAcao(),
	// acao.codigoFuncao(),
	// acao.nomeFuncao(),
	// acao.valorPpa(),
	// valorTotalLoa,
	// acao.anoAcao(),
	// detalhamentosDaAcao // detalhamentoOrcamentarioLoa
	// );
	// })
	// .toList();
	// return dadosAcoes;
	// }

	public List<AcaoPpaLoaDto> dadosAcoes(
			String ppa,
			List<Long> funcoes,
			List<Long> programas,
			List<Long> anos,
			List<Long> uos,
			List<Long> acoes) {

		String anosFormatados = formatarCodigos(anos, "%d");
		String uosFormatados = formatarCodigos(uos, "%05d");
		String funcoesFormatadas = formatarCodigos(funcoes, "%02d");
		String programasFormatados = formatarCodigos(programas, "%04d");
		String acoesFormatadas = formatarCodigos(acoes, "%04d");

		List<AcaoPpaLoaDto> dadosPpa = consultarDadosPpa(
				ppa,
				anosFormatados,
				uosFormatados,
				funcoesFormatadas,
				programasFormatados,
				acoesFormatadas,
				primeiroAno(anos));

		if (dadosPpa.isEmpty()) {
			return List.of();
		}

		List<DadosLoaBiDto> dadosLoa = consultarDadosLoa(
				anosFormatados,
				uosFormatados,
				funcoesFormatadas,
				programasFormatados,
				acoesFormatadas);

		if (dadosLoa.isEmpty()) {
			return dadosPpa;
		}

		var detalhamentosPorAcao = agruparDetalhamentosLoa(dadosLoa);

		return dadosPpa.stream()
				.map(acao -> adicionarDadosLoa(acao, detalhamentosPorAcao))
				.toList();
	}

	private String normalizarCodigo(String valor) {
		return valor == null
				? ""
				: valor.trim();
	}

	private String formatarCodigos(List<Long> valores, String formato) {

		if (valores == null || valores.isEmpty()) {
			return "-1";
		}

		return valores.stream()
				.distinct()
				.map(valor -> String.format(formato, valor))
				.collect(Collectors.joining(","));
	}

	private String primeiroAno(List<Long> anos) {
		return anos == null || anos.isEmpty()
				? ""
				: String.valueOf(anos.get(0));
	}

	private Map<ChaveAcaoLoa, List<DetalhamentoOrcamentarioLoaDto>> agruparDetalhamentosLoa(
			List<DadosLoaBiDto> dadosLoa) {

		return dadosLoa.stream()
				.collect(Collectors.groupingBy(
						item -> criarChave(
								item.unidadeOrcamentaria(),
								item.codigoPrograma(),
								item.codigoAcao()),
						Collectors.mapping(
								this::criarDetalhamentoLoa,
								Collectors.toList())));
	}

	private DetalhamentoOrcamentarioLoaDto criarDetalhamentoLoa(DadosLoaBiDto item) {
		return new DetalhamentoOrcamentarioLoaDto(
				item.codigoGrupoDespesa(),
				item.codigoModalidade(),
				item.idUso(),
				item.fonte(),
				item.valorLoa(),
				item.nomeGrupoDespesa(),
				item.nomeModalidade(),
				item.nomeIdUso(),
				item.nomeFonte());
	}

	private ChaveAcaoLoa criarChave(
			String unidadeOrcamentaria,
			String programa,
			String acao) {

		return new ChaveAcaoLoa(
				normalizarCodigo(unidadeOrcamentaria),
				normalizarCodigo(programa),
				normalizarCodigo(acao));
	}

	private List<DadosLoaBiDto> consultarDadosLoa(
			String anos,
			String uos,
			String funcoes,
			String programas,
			String acoes) {

		Map<String, Object> params = Map.of(
				"paramp_ano", anos,
				"paramp_cod_uo", uos,
				"paramp_cod_programa", programas,
				"paramp_cod_acao", acoes,
				"paramp_cod_funcao", funcoes);

		return apiUtils.consult(
				targetDadosLoa,
				dadosLoaDataAccessId,
				siscapSigefesPath,
				params,
				rs -> new DadosLoaBiDto(
						textoOuNull(rs, "orgao"),
						textoOuNull(rs, "sigla"),
						textoOuNull(rs, "nom_orgao"),

						textoOuNull(rs, "uo"),
						textoOuNull(rs, "mne_uo"),
						textoOuNull(rs, "NOM_UO"),

						textoOuNull(rs, "COD_PROGRAMA"),
						textoOuNull(rs, "NOM_PROGRAMA"),

						textoOuNull(rs, "acao"),
						textoOuNull(rs, "NOM_ACAO"),

						textoOuVazio(rs, "grupo"),
						textoOuVazio(rs, "NOM_GRUPO_DESPESA"),
						textoOuVazio(rs, "SGL_GRUPO_DESPESA"),
						textoOuVazio(rs, "modalidade"),
						textoOuVazio(rs, "iduso"),
						textoOuVazio(rs, "fonte"),

						decimalOuZero(rs, "loa_fin"),

						textoOuVazio(rs, "nome_modalidade"),
						textoOuVazio(rs, "nome_iduso"),
						textoOuVazio(rs, "nome_fonte")));

	}

	private List<AcaoPpaLoaDto> consultarDadosPpa(
			String ppa,
			String anos,
			String uos,
			String funcoes,
			String programas,
			String acoes,
			String anoAcao) {

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa,
				"paramp_ano", anos,
				"paramp_cod_uo", uos,
				"paramp_cod_programa", programas,
				"paramp_cod_acao", acoes,
				"paramp_cod_funcao", funcoes);

		return apiUtils.consult(
				targetDadosAcoesPpa,
				dadosAcoesPpaDataAccessId,
				siscapSigefesPath,
				params,
				rs -> new AcaoPpaLoaDto(
						longOuZero(rs, "cod_acao"),

						textoOuNull(rs, "cod_orgao"),
						textoOuNull(rs, "sigla"),
						textoOuNull(rs, "nom_orgao"),

						textoOuNull(rs, "cod_uo"),
						textoOuNull(rs, "mne_uo"),
						textoOuNull(rs, "nom_uo"),

						textoOuNull(rs, "cod_programa"),
						textoOuNull(rs, "nom_programa"),

						textoOuNull(rs, "cod_acao"),
						textoOuNull(rs, "nom_acao"),

						textoOuNull(rs, "cod_funcao"),
						textoOuNull(rs, "nom_funcao"),

						decimalOuZero(rs, "vlr_ppa"),

						BigDecimal.ZERO,
						anoAcao,
						List.of()));
	}

	private BigDecimal decimalOuZero(JsonNode rs, String campo) {

		JsonNode valor = rs.path(campo);

		return valor.isNumber()
				? valor.decimalValue()
				: BigDecimal.ZERO;
	}

	private String textoOuNull(Map<String, JsonNode> rs, String campo) {
		JsonNode valor = rs.get(campo);

		return valor == null || valor.isNull()
				? null
				: valor.asText();
	}

	private String textoOuVazio(Map<String, JsonNode> rs, String campo) {
		JsonNode valor = rs.get(campo);

		return valor == null || valor.isNull()
				? ""
				: valor.asText();
	}

	private BigDecimal decimalOuZero(Map<String, JsonNode> rs, String campo) {
		JsonNode valor = rs.get(campo);

		return valor != null && valor.isNumber()
				? valor.decimalValue()
				: BigDecimal.ZERO;
	}

	private Long longOuZero(Map<String, JsonNode> rs, String campo) {
		JsonNode valor = rs.get(campo);

		return valor != null && valor.isNumber()
				? valor.asLong()
				: 0L;
	}

	private AcaoPpaLoaDto adicionarDadosLoa(
			AcaoPpaLoaDto acao,
			Map<ChaveAcaoLoa, List<DetalhamentoOrcamentarioLoaDto>> detalhamentosPorAcao) {

		ChaveAcaoLoa chave = criarChave(
				acao.codigoUnidadeOrcamentaria(),
				acao.codigoPrograma(),
				acao.codigoAcao());

		List<DetalhamentoOrcamentarioLoaDto> detalhamentos = detalhamentosPorAcao.getOrDefault(chave, List.of());

		BigDecimal valorTotalLoa = detalhamentos.stream()
				.filter(Objects::nonNull)
				.map(DetalhamentoOrcamentarioLoaDto::valor)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new AcaoPpaLoaDto(
				acao.id(),
				acao.codigoOrgao(),
				acao.siglaOrgao(),
				acao.nomeOrgao(),
				acao.codigoUnidadeOrcamentaria(),
				acao.siglaUnidadeOrcamentaria(),
				acao.nomeUnidadeOrcamentaria(),
				acao.codigoPrograma(),
				acao.nomePrograma(),
				acao.codigoAcao(),
				acao.nomeAcao(),
				acao.codigoFuncao(),
				acao.nomeFuncao(),
				acao.valorPpa(),
				valorTotalLoa,
				acao.anoAcao(),
				detalhamentos);
	}

}