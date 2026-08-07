package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.AcaoPpaLoaDto;
import br.gov.es.siscap.dto.ChaveAcaoLoa;
import br.gov.es.siscap.dto.DetalhamentoOrcamentarioLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPeriodoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPpaLoaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
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

	// PpaLoaBiService(SiscapApplication siscapApplication) {
	// this.siscapApplication = siscapApplication;
	// }

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

	public List<OpcoesPpaLoaDto> listarUosAnoPpa(Long ano) {

		Map<String, Object> params = Map.of(
				"paramp_ano", ano);

		String pmoPath = siscapSigefesPath;
		String target = targetUosPpa;
		String dataAccessId = uosPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_uo").asLong(),
						rs.get("mne_uo").asText()));

	}

	public List<OpcoesPpaLoaDto> listarFuncoes(List<Long> anos, List<Long> uos) {

		String anosFormatados = anos.stream()
				.distinct()
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ano", anosFormatados,
				"paramp_cod_uo", uosFormatados);

		String pmoPath = siscapSigefesPath;
		String target = targetFuncoesPpa;
		String dataAccessId = funcoesPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_funcao").asLong(),
						rs.get("nom_funcao").asText()));

	}

	public List<OpcoesPpaLoaDto> listarProgramas(List<Long> anos, List<Long> uos, List<Long> funcoes) {

		String anosFormatados = anos.stream()
				.distinct()
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.distinct()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ano", anosFormatados,
				"paramp_cod_uo", uosFormatados,
				"paramp_cod_funcao", funcoesFormatadas);

		String pmoPath = siscapSigefesPath;
		String target = targetProgramasPpa;
		String dataAccessId = programasPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_programa").asLong(),
						rs.get("nom_programa").asText()));

	}

	public List<OpcoesPpaLoaDto> listarAcoes(List<Long> funcoes, List<Long> programas, List<Long> anos,
			List<Long> uos) {

		String anosFormatados = anos.stream()
				.distinct()
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.distinct()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		String programasFormatados = programas.stream()
				.distinct()
				.map(programa -> String.format("%04d", programa))
				.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ano", anosFormatados,
				"paramp_cod_uo", uosFormatados,
				"paramp_cod_funcao", funcoesFormatadas,
				"paramp_cod_programa", programasFormatados);

		String pmoPath = siscapSigefesPath;
		String target = targetAcoesPpa;
		String dataAccessId = acoesPpaDataAccessId;

		return apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new OpcoesPpaLoaDto(
						rs.get("cod_acao").asLong(),
						rs.get("nom_acao").asText()));

	}

	public List<AcaoPpaLoaDto> dadosAcoes(String ppa, List<Long> funcoes, List<Long> programas, List<Long> anos,
			List<Long> uos,
			List<Long> acoes) {

		String anosFormatados = anos.stream()
				.distinct()
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.distinct()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.distinct()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		String programasFormatados = programas.stream()
				.distinct()
				.map(programa -> String.format("%04d", programa))
				.collect(Collectors.joining(","));

		String acoesFormatadas = acoes.stream()
				.distinct()
				.map(acao -> String.format("%04d", acao))
				.collect(Collectors.joining(","));

		Map<String, Object> params = Map.of(
				"paramp_ppa", ppa,
				"paramp_ano", anosFormatados,
				"paramp_cod_uo", uosFormatados,
				"paramp_cod_programa", programasFormatados,
				"paramp_cod_acao", acoesFormatadas,
				"paramp_cod_funcao", funcoesFormatadas);

		String pmoPath = siscapSigefesPath;
		String target = targetDadosAcoesPpa;
		String dataAccessId = dadosAcoesPpaDataAccessId;

		List<AcaoPpaLoaDto> dadosAcoes = apiUtils.consult(target, dataAccessId, pmoPath, params,
				rs -> new AcaoPpaLoaDto(

						rs.get("cod_acao").asLong(), // id

						rs.get("cod_orgao").asText(null),
						rs.get("sigla").asText(null),
						rs.get("nom_orgao").asText(null),

						rs.get("cod_uo").asText(null),
						rs.get("mne_uo").asText(null),
						rs.get("nom_uo").asText(null),

						rs.get("cod_programa").asText(null),
						rs.get("nom_programa").asText(null),

						rs.get("cod_acao").asText(null),
						rs.get("nom_acao").asText(null),

						rs.get("cod_funcao").asText(null),
						rs.get("nom_funcao").asText(null),

						rs.get("vlr_ppa").decimalValue(),

						BigDecimal.ZERO,

						rs.get("ano_acao").asText(null),

						List.of() // detalhamentoOrcamentarioLoa

				));

		if (dadosAcoes.isEmpty()) {
			throw new ValidacaoSiscapException(Arrays.asList("Nenhum dado encontrado para os filtros informados."));
		}

		String targetLoa = targetDadosLoa;
		String dataAccessIdLoa = dadosLoaDataAccessId;

		List<DadosLoaBiDto> dadosLoa = apiUtils.consult(targetLoa, dataAccessIdLoa, pmoPath, params,
				rs -> new DadosLoaBiDto(
						rs.get("orgao").asText(null), // orgao
						rs.get("sigla").asText(null), // sigla
						rs.get("nom_orgao").asText(null), // nomeOrgao
						rs.get("uo").asText(null), // unidadeOrcamentaria
						rs.get("mne_uo").asText(null), // siglaUnidadeOrcamentaria
						rs.get("NOM_UO").asText(null), // nomeUnidadeOrcamentaria
						rs.get("COD_PROGRAMA").asText(null), // codigoPrograma
						rs.get("NOM_PROGRAMA").asText(null), // nomePrograma
						rs.get("acao").asText(null), // codigoAcao
						rs.get("NOM_ACAO").asText(null), // nomeAcao
						rs.get("grupo").asText(null), // codigoGrupoDespesa
						rs.get("NOM_GRUPO_DESPESA").asText(null), // nomeGrupoDespesa
						rs.get("SGL_GRUPO_DESPESA").asText(null), // siglaGrupoDespesa
						rs.get("modalidade").asText(null), // codigoModalidade
						rs.get("iduso").asText(null), // idUso
						rs.get("fonte").asText(null), // fonte
						rs.get("loa_fin").decimalValue() // valorLoa
				));

		if (dadosLoa.isEmpty()) {
			throw new ValidacaoSiscapException(Arrays.asList("Nenhum dado encontrado para os filtros informados."));
		}

		Map<ChaveAcaoLoa, List<DetalhamentoOrcamentarioLoaDto>> detalhamentosPorAcao = dadosLoa.stream()
				.collect(Collectors.groupingBy(
						item -> new ChaveAcaoLoa(
								normalizarCodigo(item.unidadeOrcamentaria()),
								normalizarCodigo(item.codigoPrograma()),
								normalizarCodigo(item.codigoAcao())),
						Collectors.mapping(
								item -> new DetalhamentoOrcamentarioLoaDto(
										item.codigoGrupoDespesa(),
										item.codigoModalidade(),
										item.idUso(),
										item.fonte(),
										item.valorLoa()),
								Collectors.toList())));

		if (detalhamentosPorAcao.isEmpty()) {
			throw new ValidacaoSiscapException(
					Arrays.asList("Nenhum detalhamento orçamentário encontrado para os filtros informados."));
		}

		dadosAcoes = dadosAcoes.stream()
				.map(acao -> {

					ChaveAcaoLoa chave = new ChaveAcaoLoa(
							normalizarCodigo(acao.codigoUnidadeOrcamentaria()),
							normalizarCodigo(acao.codigoPrograma()),
							normalizarCodigo(acao.codigoAcao()));

					List<DetalhamentoOrcamentarioLoaDto> detalhamentosDaAcao = detalhamentosPorAcao.getOrDefault(chave,
							List.of());

					BigDecimal valorTotalLoa = Optional
							.ofNullable(detalhamentosDaAcao)
							.orElseGet(List::of)
							.stream()
							.filter(Objects::nonNull) // remove DTOs nulos
							.map(DetalhamentoOrcamentarioLoaDto::valor)
							.filter(Objects::nonNull) // remove valores nulos
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
							detalhamentosDaAcao // detalhamentoOrcamentarioLoa
					);

				})
				.toList();

		return dadosAcoes;

	}

	private String normalizarCodigo(String valor) {
		return valor == null
				? ""
				: valor.trim();
	}

}