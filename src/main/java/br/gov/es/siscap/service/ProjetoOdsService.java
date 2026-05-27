package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.OdsPentahoBiDto;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoOds;
import br.gov.es.siscap.repository.ProjetoOdsRepository;
import br.gov.es.siscap.utils.pentaho.ApiUtils;
import br.gov.es.siscap.utils.pentaho.PentahoBIService;
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

	// private void validarOdsSelecionadas(ProjetoIndicadorDto indicadorDto) {
	// if (indicadorDto.odsSelecionadas() == null ||
	// indicadorDto.odsSelecionadas().isEmpty()) {
	// throw new SiscapServiceException(Arrays.asList("É obrigatório selecionar pelo
	// menos uma ODS para o indicador."));
	// }
	// if (indicadorDto.idIndicadorExterno() == null) {
	// throw new SiscapServiceException(Arrays.asList("Indicador externo é
	// obrigatório para vincular ODS."));
	// }
	// if (!indicadorDto.odsSelecionadas().isEmpty()) {
	// boolean existeOdsInvalida = indicadorDto.odsSelecionadas()
	// .stream()
	// .anyMatch(odsDto -> !odsIndicadorExternoRepository
	// .existsByIdAndIndicadorExternoId(
	// odsDto.idOdsIndicadorExterno(),
	// indicadorDto.idIndicadorExterno()
	// ));
	// if (existeOdsInvalida) {
	// throw new SiscapServiceException(
	// Arrays.asList("Uma ou mais ODS selecionadas não pertencem ao indicador
	// informado.")
	// );
	// }
	// }
	// }

	// private ProjetoIndicadorOds criarProjetoIndicadorOds(
	// ProjetoIndicador projetoIndicador,
	// ProjetoIndicadorOdsDto odsDto
	// ) {
	// OdsIndicadorExterno odsIndicadorExterno = odsIndicadorExternoRepository
	// .findById(odsDto.idOdsIndicadorExterno())
	// .orElseThrow(() -> new SiscapServiceException(Arrays.asList("ODS vinculada ao
	// indicador externo não encontrada.")));
	// if (!odsIndicadorExterno.getIndicadorExterno().getId()
	// .equals(projetoIndicador.getIndicadorExterno().getId())) {
	// throw new SiscapServiceException(Arrays.asList("A ODS selecionada não
	// pertence ao indicador informado."));
	// }
	// ProjetoIndicadorOds projetoIndicadorOds = new ProjetoIndicadorOds();
	// projetoIndicadorOds.setProjetoIndicador(projetoIndicador);
	// projetoIndicadorOds.setOdsIndicadorExterno(odsIndicadorExterno);
	// return projetoIndicadorOds;
	// }

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

	// private void validarOdsPertencemAoIndicador(
	// ProjetoIndicador projetoIndicador,
	// Set<Integer> idsOdsIndicadorExterno
	// ) {
	// Integer idIndicadorExterno = projetoIndicador.getIndicadorExterno().getId();
	// boolean existeOdsInvalida = idsOdsIndicadorExterno.stream()
	// .anyMatch(idOdsIndicadorExterno ->
	// !odsIndicadorExternoRepository.existsByIdAndIndicadorExternoId(
	// idOdsIndicadorExterno,
	// idIndicadorExterno )
	// );
	// if (existeOdsInvalida) {
	// throw new SiscapServiceException(Arrays.asList("Uma ou mais ODS selecionadas
	// não pertencem ao indicador informado."));
	// }
	// }

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
				.filter(ind -> ind.getId() != null)
				.collect(Collectors.toMap(ProjetoOds::getId, Function.identity()));

		return dtoList.stream()
				.map(dto -> {
					if (dto.odsId() == null) {
						throw new IllegalArgumentException("Id da Ods do BI não pode ser null");
					}
					ProjetoOds ods;
					if (dto.idOdsProjeto() != null && odsExistentesMap.containsKey(dto.idOdsProjeto())) {
						ods = odsExistentesMap.get(dto.idOdsProjeto());
					} else {
						ods = new ProjetoOds();
						ods.setProjeto(projeto);
						// ods.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
					}
					// ods.setTipoOds(dto.tipoOds());
					// ods.setDescricaoOds(dto.descricaoOds());
					// ods.setDescricaoMeta(dto.descricaoMeta());
					// ods.setIdOdsIndicadorExterno(dto.idOdsIndicadorExterno());
					return ods;
				})
				.collect(Collectors.toSet());

	}

	public List<ProjetoOdsDto> buscarDadosOdsBi(List<Integer> odsIds) {

		List<OdsPentahoBiDto> odsBiList = this.listarOdsPorIds(odsIds);

		// return odsBiList.stream()
		// 		.filter(ods -> odsIds.contains(ods.odsId()))
		// 		.map(ods -> new ProjetoOdsDto(
		// 				ods.odsId(),
		// 				ods.ordemOds(),
		// 				ods.nomeOds(),
		// 				ods.descricaoOds()))
		// 		.toList();

		return odsBiList.stream()
        .filter(ods -> odsIds.contains(ods.odsId()))
        .collect(Collectors.toMap(
                OdsPentahoBiDto::odsId,
                ods -> new ProjetoOdsDto(
                        ods.odsId(),
                        ods.ordemOds(),
                        ods.nomeOds(),
                        ods.descricaoOds()
                ),
                (existente, repetido) -> existente
        ))
        .values()
        .stream()
        .toList();

	}

	private List<OdsPentahoBiDto> listarOdsPorIds(List<Integer> odsIds) {

		Map<String, Object> params = Map.of();
		// "ids",
		// odsIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

		return pentahoBIService.consult(targetOds, odsDataAccessId, siscapPath, params,
				rs -> new OdsPentahoBiDto(
						rs.get("IndicadorId").asInt(),
						rs.get("OdsId").asInt(),
						rs.get("DescricaoOds").asText(),
						rs.get("nomeOds").asText(),
						rs.get("ordemOds").asInt()));

	}

}