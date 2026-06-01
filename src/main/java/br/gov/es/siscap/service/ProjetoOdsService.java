package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoOdsDto;
import br.gov.es.siscap.dto.indicadoresexternos.OdsPentahoBiDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoOds;
import br.gov.es.siscap.repository.ProjetoOdsRepository;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoOdsService {

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

	private final ProjetoOdsRepository projetoOdsRepository;
	private final ApiUtils pentahoBIService;

	private final Logger logger = LogManager.getLogger(ProjetoOdsService.class);

	public Set<ProjetoOds> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando ods´s do Projeto com id: {}", projeto.getId());
		return this.projetoOdsRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoOds> cadastrar(Projeto projeto, List<ProjetoOdsDto> projetoOdsDtoList) {

		logger.info("Cadastrando ODS´s do Projeto com id: {}", projeto.getId());

		Set<ProjetoOds> projetoOdsSet = new HashSet<>();

		logger.info("Lista de ODS vindas do front : {}", projetoOdsDtoList);

		projetoOdsDtoList.forEach(odsDto -> {
			ProjetoOds odsProjeto = new ProjetoOds(projeto, odsDto);
			projetoOdsSet.add(odsProjeto);
		});

		List<ProjetoOds> projetoOdsList = projetoOdsRepository.saveAll(projetoOdsSet);

		logger.info("Ods do projeto cadastrados com sucesso");

		return new HashSet<>(projetoOdsList);

	}

	@Transactional
	public Set<ProjetoOds> atualizar(Projeto projeto, List<ProjetoOdsDto> projetoOdsDtoList) {

		logger.info("Alterando dados de ODS´s do Projeto com id: {}", projeto.getId());

		Set<ProjetoOds> projetoOdsSet = this.buscarPorProjeto(projeto);

		Set<ProjetoOds> odsProjetoAtualizarSet = this.atualizarOdsProjeto(projeto,
				projetoOdsSet, projetoOdsDtoList);

		odsProjetoAtualizarSet.forEach(indicadorProjeto -> {
			ProjetoOdsDto odsDto = projetoOdsDtoList.stream()
					.filter(dto -> Objects.equals(dto.idOdsProjeto(), indicadorProjeto.getId()))
					.findFirst()
					.orElse(null);
			if (odsDto != null) {
				sincronizarOdsSelecionadas(indicadorProjeto, odsDto);
			}
		});

		projetoOdsRepository.saveAllAndFlush(odsProjetoAtualizarSet);

		Set<Integer> idsDto = projetoOdsDtoList.stream()
				.map(ProjetoOdsDto::idOdsProjeto)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<ProjetoOds> odsParaRemover = projetoOdsSet.stream()
				.filter(indicador -> !idsDto.contains(indicador.getId()))
				.collect(Collectors.toSet());

		projetoOdsRepository.deleteAll(odsParaRemover);

		logger.info("ODS do projeto alteradas com sucesso");

		return this.buscarPorProjeto(projeto);

	}

	private void sincronizarOdsSelecionadas(
			ProjetoOds projetoOds,
			ProjetoOdsDto odsDto) {

		// Set<Integer> idsOdsSelecionadasDto = indicadorDto.odsSelecionadas()
		// .stream()
		// .map(ProjetoIndicadorOdsDto::idOdsIndicadorExterno)
		// .filter(Objects::nonNull)
		// .collect(Collectors.toSet());

		// validarOdsPertencemAoIndicador(projetoIndicador, idsOdsSelecionadasDto);

		// projetoIndicador.getOdsSelecionadas().removeIf( odsAtual ->
		// !idsOdsSelecionadasDto.contains(
		// odsAtual.getOdsIndicadorExterno().getId()) );

		// Set<Integer> idsOdsJaExistentes = projetoIndicador.getOdsSelecionadas()
		// .stream()
		// .map(odsAtual -> odsAtual.getOdsIndicadorExterno().getId())
		// .collect(Collectors.toSet());

		// idsOdsSelecionadasDto.stream()
		// .filter(idOdsIndicadorExterno ->
		// !idsOdsJaExistentes.contains(idOdsIndicadorExterno))
		// .forEach(idOdsIndicadorExterno -> {
		// OdsIndicadorExterno odsIndicadorExterno = odsIndicadorExternoRepository
		// .findById(idOdsIndicadorExterno)
		// .orElseThrow(() -> new SiscapServiceException(Arrays.asList("ODS vinculada ao
		// indicador não encontrada.")));
		// ProjetoIndicadorOds novaOds = new ProjetoIndicadorOds();
		// novaOds.setProjetoIndicador(projetoIndicador);
		// novaOds.setOdsIndicadorExterno(odsIndicadorExterno);
		// projetoIndicador.getOdsSelecionadas().add(novaOds);
		// });

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo ods´s do Projeto com id: {}", projeto.getId());

		Set<ProjetoOds> projetoOdsSet = this.buscarPorProjeto(projeto);

		if (projetoOdsSet.isEmpty()) {
			logger.info("Nenhum ODS encontrado para o projeto com id: {}", projeto.getId());
			return;
		}

		List<ProjetoOds> projetoOdsList = projetoOdsRepository.saveAllAndFlush(projetoOdsSet);

		projetoOdsRepository.deleteAll(projetoOdsList);

		logger.info("Ods do projeto excluídos com sucesso");

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {
		logger.info("Excluindo fisicamente ods´s do Projeto com id: {}", projeto.getId());

		projetoOdsRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Ods do projeto excluídos fisicamente com sucesso");
	}

	private Set<ProjetoOds> atualizarOdsProjeto(
			Projeto projeto,
			Set<ProjetoOds> odsExistentes,
			List<ProjetoOdsDto> dtoList) {

		Map<Integer, ProjetoOds> odsExistentesMap = odsExistentes.stream()
				.filter(ods -> ods.getId() != null)
				.collect(Collectors.toMap(ProjetoOds::getId, Function.identity()));

		return dtoList.stream()
				.map(dto -> {

					if (dto.odsId() == null) {
						throw new ValidacaoSiscapException(List.of("Id da ODS do BI não pode ser null."));
					}

					ProjetoOds ods;

					if (dto.idOdsProjeto() != null && odsExistentesMap.containsKey(dto.idOdsProjeto())) {
						ods = odsExistentesMap.get(dto.idOdsProjeto());
					} else {
						ods = new ProjetoOds();
						ods.setProjeto(projeto);
					}

					ods.setIdOds(dto.odsId());

					return ods;
				})
				.collect(Collectors.toSet());

	}

	public List<ProjetoOdsDto> buscarDadosOdsBi(List<Integer> odsIds) {

		List<OdsPentahoBiDto> odsBiList = this.listarOdsPorIds(odsIds);

		return odsBiList.stream()
				.filter(ods -> odsIds.contains(ods.odsId()))
				.collect(Collectors.toMap(
						OdsPentahoBiDto::odsId,
						ods -> new ProjetoOdsDto(
								ods.odsId(),
								ods.ordemOds(),
								ods.nomeOds(),
								ods.descricaoOds(),
								obterCorOds(ods.ordemOds())),
						(existente, repetido) -> existente))
				.values()
				.stream()
				.toList();

	}

	private List<OdsPentahoBiDto> listarOdsPorIds(List<Integer> odsIds) {

		String parametroOds = odsIds != null && !odsIds.isEmpty()
				? odsIds.stream()
						.map(String::valueOf)
						.distinct()
						.collect(Collectors.joining(","))
				: "-1";

		Map<String, Object> params = Map.of(
				"paramp_ods", parametroOds,
				"paramp_indicadores", "-1");

		return pentahoBIService.consult(targetOds, odsDataAccessId, siscapPath, params,
				rs -> new OdsPentahoBiDto(
						rs.get("OdsId").asInt(),
						rs.get("DescricaoOds").asText(),
						rs.get("nomeOds").asText(),
						rs.get("ordemOds").asInt(),
						obterCorOds(rs.get("ordemOds").asInt())));

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

}