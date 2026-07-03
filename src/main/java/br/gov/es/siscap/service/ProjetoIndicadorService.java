package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorCatalogoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.enums.TipoStatusEnum;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;
import br.gov.es.siscap.models.TipoStatus;
import br.gov.es.siscap.repository.ProjetoIndicadorMetaRepository;
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
public class ProjetoIndicadorService {

	private final ProjetoIndicadorRepository projetoIndicadorRepository;
	private final ProjetoIndicadorMetaRepository projetoIndicadorExternoMeta;
	private final Logger logger = LogManager.getLogger(ProjetoIndicadorService.class);

	public Set<ProjetoIndicador> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando indicadores do Projeto com id: {}", projeto.getId());
		return this.projetoIndicadorRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoIndicador> cadastrar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {

		logger.info("Cadastrando indicadores do Projeto com id: {}", projeto.getId());

		Set<ProjetoIndicador> projetoIndicadorSet = new HashSet<>();

		logger.info("Lista de indicadores vindas do front : {}", projetoIndicadorDtoList);

		projetoIndicadorDtoList.forEach(indicadorDto -> {
			ProjetoIndicador indicadorProjeto = new ProjetoIndicador(projeto, indicadorDto);
			projetoIndicadorSet.add(indicadorProjeto);
		});

		List<ProjetoIndicador> projetoIndicadorList = projetoIndicadorRepository.saveAll(projetoIndicadorSet);

		List<ProjetoIndicador> projetoIndicadorListAtualizada = projetoIndicadorRepository
				.saveAll(projetoIndicadorList);

		logger.info("Indicadores do projeto cadastrados com sucesso");

		return new HashSet<>(projetoIndicadorListAtualizada);

	}
	
	@Transactional
	public Set<ProjetoIndicador> atualizar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {

		logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());

		Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);

		Set<ProjetoIndicador> indicadoresProjetoAtualizarSet = this.atualizarIndicadoresProjeto(projeto,
				projetoIndicadorSet, projetoIndicadorDtoList);

		// indicadoresProjetoAtualizarSet.forEach(indicadorProjeto -> {
		// 	ProjetoIndicadorDto indicadorDto = projetoIndicadorDtoList.stream()
		// 			.filter(dto -> Objects.equals(dto.idIndicador(), indicadorProjeto.getId()))
		// 			.findFirst()
		// 			.orElse(null);
		// 	if (indicadorDto != null) {
		// 		sincronizarOdsSelecionadas(indicadorProjeto, indicadorDto);
		// 	}
		// });

		projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);

		Set<Integer> idsDto = projetoIndicadorDtoList.stream()
				.map(ProjetoIndicadorDto::idIndicador)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<ProjetoIndicador> indicadoresParaRemover = projetoIndicadorSet.stream()
				.filter(indicador -> !idsDto.contains(indicador.getId()))
				.collect(Collectors.toSet());

		projetoIndicadorRepository.deleteAll(indicadoresParaRemover);

		logger.info("Indicadores do projeto alterados com sucesso");

		return this.buscarPorProjeto(projeto);

	}

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

		projetoIndicadorExternoMeta.deleteFisicoPorProjeto(projeto.getId());

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

					if (dto.id() != null && existentesMap.containsKey(dto.id())) {
						meta = existentesMap.get(dto.id());
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