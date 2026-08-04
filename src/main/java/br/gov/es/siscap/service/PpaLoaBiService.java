package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.AcaoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPeriodoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPpaLoaDto;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private final ApiUtils apiUtils;

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
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
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
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
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
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		String programasFormatados = programas.stream()
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

	public List<AcaoPpaLoaDto> dadosAcoes(List<Long> funcoes, List<Long> programas, List<Long> anos, List<Long> uos,
			List<Long> acoes) {

		String anosFormatados = anos.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		String uosFormatados = uos.stream()
				.map(uo -> String.format("%05d", uo))
				.collect(Collectors.joining(","));

		String funcoesFormatadas = funcoes.stream()
				.map(funcao -> String.format("%02d", funcao))
				.collect(Collectors.joining(","));

		String programasFormatados = programas.stream()
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
		
		List<AcaoPpaLoaDto> dadosAcoes = apiUtils.consult(target, dataAccessId, pmoPath, params,
			rs -> new AcaoPpaLoaDto(
				rs.get("cod_acao").asLong(),                  				  // id
				rs.get("cod_acao").asText(null),               // codigo
				rs.get("nom_acao").asText(null),               // titulo
				rs.get("dsc_acao").asText(null),               // descricao
				rs.get("unidade_orcamentaria").asText(null),   // unidadeOrcamentaria
				rs.get("orgao").asText(null),                  // orgao
				rs.get("funcao").asText(null),                 // funcao
				rs.get("programa").asText(null),               // programa
				rs.get("periodo_ppa").asText(null),            // periodoPpa
				rs.get("valor_ppa").decimalValue(),            			 // valorPpa
				rs.get("ano_loa").asInt(),                     			 // anoLoa
				rs.get("valor_loa").decimalValue(),            			 // valorLoa
				List.of()                                      					// detalhamentoOrcamentarioLoa
			)
		);

		return dadosAcoes;
		
	}

}