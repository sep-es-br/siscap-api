package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorCatalogoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;
import br.gov.es.siscap.models.ProjetoOds;
import br.gov.es.siscap.models.TipoStatus;
import br.gov.es.siscap.repository.ProjetoIndicadorRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoOdsService {

	private final ProjetoOdsRepository projetoOdsRepository;
	private final Logger logger = LogManager.getLogger(ProjetoOdsService.class);
	
	public Set<ProjetoOds> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando ods´s do Projeto com id: {}", projeto.getId());
		return this.projetoOdsRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoIndicador> cadastrar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {

		logger.info("Cadastrando indicadores do Projeto com id: {}", projeto.getId());

		Set<ProjetoIndicador> projetoIndicadorSet = new HashSet<>();

		logger.info("Lista de indicadores vindas do front : {}", projetoIndicadorDtoList);

		projetoIndicadorDtoList.forEach(indicadorDto -> {
			ProjetoIndicador indicadorProjeto = new ProjetoIndicador(projeto, indicadorDto);
			// if (indicadorDto.idIndicadorExterno() != null) {
			// IndicadorExterno indicador = indicadorExternoRepository
			// .findById(indicadorDto.idIndicadorExterno())
			// .orElseThrow(() -> new RuntimeException("Indicador externo não encontrado"));
			// indicadorProjeto.setIdIndicadorExterno(indicador.getId());
			// }
			projetoIndicadorSet.add(indicadorProjeto);
		});

		List<ProjetoIndicador> projetoIndicadorList = projetoIndicadorRepository.saveAll(projetoIndicadorSet);

		// for (ProjetoIndicador projetoIndicador : projetoIndicadorList) {

		// ProjetoIndicadorDto indicadorDto = projetoIndicadorDtoList.stream()
		// .filter(dto -> Objects.equals(dto.idIndicadorExterno(),
		// projetoIndicador.getIndicadorExterno() != null
		// ? projetoIndicador.getIndicadorExterno().getId()
		// : null))
		// .findFirst()
		// .orElseThrow(() -> new RuntimeException("DTO do indicador não encontrado"));

		// // validarOdsSelecionadas(indicadorDto);

		// // Set<ProjetoIndicadorOds> odsSelecionadas = indicadorDto.odsSelecionadas()
		// // .stream()
		// // .map( odsDto -> criarProjetoIndicadorOds(projetoIndicador, odsDto))
		// // .collect(Collectors.toSet());

		// // projetoIndicador.setOdsSelecionadas(odsSelecionadas);

		// }

		List<ProjetoIndicador> projetoIndicadorListAtualizada = projetoIndicadorRepository
				.saveAll(projetoIndicadorList);

		logger.info("Indicadores do projeto cadastrados com sucesso");

		return new HashSet<>(projetoIndicadorListAtualizada);

		// return new HashSet<>(projetoIndicadorList);

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

		// Set<ProjetoIndicador> indicadoresProjetoAtualizarSet = this.atualizarIndicadoresProjeto(projeto,
		// 		projetoIndicadorSet, projetoIndicadorDtoList);

		// indicadoresProjetoAtualizarSet.forEach(indicadorProjeto -> {
		// 	ProjetoIndicadorDto indicadorDto = projetoIndicadorDtoList.stream()
		// 			.filter(dto -> Objects.equals(dto.idIndicador(), indicadorProjeto.getId()))
		// 			.findFirst()
		// 			.orElse(null);
		// 	if (indicadorDto != null) {
		// 		sincronizarOdsSelecionadas(indicadorProjeto, indicadorDto);
		// 	}
		// });

		// projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);
		// Set<Integer> idsDto = projetoIndicadorDtoList.stream()
		// 		.map(ProjetoIndicadorDto::idIndicador)
		// 		.filter(Objects::nonNull)
		// 		.collect(Collectors.toSet());
		// Set<ProjetoIndicador> indicadoresParaRemover = projetoIndicadorSet.stream()
		// 		.filter(indicador -> !idsDto.contains(indicador.getId()))
		// 		.collect(Collectors.toSet());
		// projetoIndicadorRepository.deleteAll(indicadoresParaRemover);
		// logger.info("Indicadores do projeto alterados com sucesso");

		return this.buscarPorProjeto(projeto);

	}

	private void sincronizarOdsSelecionadas(
			ProjetoIndicador projetoIndicador,
			ProjetoIndicadorDto indicadorDto) {

		// if (indicadorDto.odsSelecionadas() == null ||
		// indicadorDto.odsSelecionadas().isEmpty()) {
		// throw new SiscapServiceException(Arrays.asList("É obrigatório selecionar pelo
		// menos uma ODS para o indicador."));
		// }

		// if (projetoIndicador.getIndicadorExterno() == null) {
		// throw new SiscapServiceException(Arrays.asList("Indicador externo é
		// obrigatório para vincular ODS."));
		// }

		// Set<Integer> idsOdsSelecionadasDto = indicadorDto.odsSelecionadas()
		// .stream()
		// .map(ProjetoIndicadorOdsDto::idOdsIndicadorExterno)
		// .filter(Objects::nonNull)
		// .collect(Collectors.toSet());

		// if (idsOdsSelecionadasDto.isEmpty()) {
		// throw new SiscapServiceException(Arrays.asList("É obrigatório selecionar pelo
		// menos uma ODS válida para o indicador."));
		// }

		// validarOdsPertencemAoIndicador(projetoIndicador, idsOdsSelecionadasDto);

		// projetoIndicador.getOdsSelecionadas().removeIf(odsAtual ->
		// !idsOdsSelecionadasDto.contains(
		// odsAtual.getOdsIndicadorExterno().getId()
		// )
		// );

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
	// idIndicadorExterno
	// )
	// );
	// if (existeOdsInvalida) {
	// throw new SiscapServiceException(Arrays.asList("Uma ou mais ODS selecionadas
	// não pertencem ao indicador informado."));
	// }
	// }

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo indicadores do Projeto com id: {}", projeto.getId());

		Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);

		List<ProjetoIndicador> projetoIndicadorList = projetoIndicadorRepository.saveAllAndFlush(projetoIndicadorSet);

		projetoIndicadorRepository.deleteAll(projetoIndicadorList);

		logger.info("Indicadores do projeto excluídos com sucesso");

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {
		logger.info("Excluindo fisicamente indicadores do Projeto com id: {}", projeto.getId());

		projetoIndicadorRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Indicadores do projeto excluídos fisicamente com sucesso");
	}

	private Set<ProjetoIndicador> atualizarIndicadoresProjeto(
			Projeto projeto,
			Set<ProjetoIndicador> indicadoresExistentes,
			List<ProjetoIndicadorDto> dtoList) {

		Map<Integer, ProjetoIndicador> indicadoresExistentesMap = indicadoresExistentes.stream()
				.filter(ind -> ind.getId() != null)
				.collect(Collectors.toMap(ProjetoIndicador::getId, Function.identity()));

		return dtoList.stream()
				.map(dto -> {

					if (dto.idIndicadorExterno() == null) {
						throw new IllegalArgumentException("Id do indicador externo não pode ser null");
					}

					ProjetoIndicador indicador;

					if (dto.idIndicador() != null && indicadoresExistentesMap.containsKey(dto.idIndicador())) {
						indicador = indicadoresExistentesMap.get(dto.idIndicador());
					} else {
						indicador = new ProjetoIndicador();
						indicador.setProjeto(projeto);
						indicador.setTipoStatus(new TipoStatus(TipoStatusEnum.ATIVO.getValue()));
					}

					indicador.setTipoIndicador(dto.tipoIndicador());
					indicador.setDescricaoIndicador(dto.descricaoIndicador());
					indicador.setDescricaoMeta(dto.descricaoMeta());
					indicador.setIdIndicadorExterno(dto.idIndicadorExterno());

					if (dto.idStatus() != null) {
						indicador.setTipoStatus(new TipoStatus(dto.idStatus()));
					}

					atualizarMetas(indicador, dto.metasIndicadorProjeto());

					return indicador;
				})
				.collect(Collectors.toSet());
				
	}

	private void atualizarMetas(ProjetoIndicador indicador, List<ProjetoIndicadorCatalogoMetaDto> metasDto) {

		Map<Integer, ProjetoIndicadorExternoMeta> existentesMap = indicador.getMetas().stream()
				.filter(m -> m.getId() != null)
				.collect(Collectors.toMap(ProjetoIndicadorExternoMeta::getId, Function.identity()));

		Set<ProjetoIndicadorExternoMeta> novasMetas = metasDto.stream()
				.map(dto -> {
					ProjetoIndicadorExternoMeta meta;

					if (dto.idFato() != null && existentesMap.containsKey(dto.idFato())) {
						meta = existentesMap.get(dto.idFato());
						meta.setValorMeta(dto.valorMeta());
						meta.setAnoMeta(dto.anoMeta());
					} else {
						meta = new ProjetoIndicadorExternoMeta(dto);
						meta.setProjetoIndicador(indicador);
					}

					return meta;
				})
				.collect(Collectors.toSet());

		indicador.getMetas().clear();
		indicador.getMetas().addAll(novasMetas);
	}

}