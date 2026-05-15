package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import br.gov.es.siscap.models.IndicadorAvulso;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicadorAvulso;
import br.gov.es.siscap.repository.IndicadorAvulsoRepository;
import br.gov.es.siscap.repository.ProjetoIndicadorAvulsoRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoIndicadorAvulsoService {

	private final ProjetoIndicadorAvulsoRepository projetoIndicadorAvulsoRepository;
	private final IndicadorAvulsoRepository indicadorAvulsoRepository;
	private final Logger logger = LogManager.getLogger(ProjetoIndicadorAvulsoService.class);

	public Set<ProjetoIndicadorAvulso> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando indicadores avulsos do Projeto com id: {}", projeto.getId());
		return this.projetoIndicadorAvulsoRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoIndicadorAvulso> sincronizar(Projeto projeto,
			List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {

		logger.info("Sincronizando indicadores avulsos do Projeto com id: {}", projeto.getId());
		logger.info("Lista de indicadores avulsos vindos do front: {}", projetoIndicadorAvulsoDtoList);

		removerIndicadoresNaoEnviados(projeto, projetoIndicadorAvulsoDtoList);

		Set<ProjetoIndicadorAvulso> projetoIndicadorAvulsoSet = new HashSet<>();

		projetoIndicadorAvulsoDtoList.forEach(indicadorDto -> {

			IndicadorAvulso indicadorAvulso;

			if (indicadorDto.idIndicadorAvulso() != null) {

				indicadorAvulso = indicadorAvulsoRepository
						.findById(indicadorDto.idIndicadorAvulso())
						.orElseThrow(() -> new RuntimeException("Indicador avulso não encontrado."));

			} else {

				indicadorAvulso = indicadorAvulsoRepository
						.save(new IndicadorAvulso(indicadorDto));

			}

			ProjetoIndicadorAvulso projetoIndicadorAvulso = new ProjetoIndicadorAvulso(
					projeto,
					indicadorAvulso,
					indicadorDto);

			projetoIndicadorAvulsoSet.add(projetoIndicadorAvulso);

		});

		List<ProjetoIndicadorAvulso> projetoIndicadorAvulsoList = projetoIndicadorAvulsoRepository
				.saveAll(projetoIndicadorAvulsoSet);

		logger.info("Indicadores avulsos sincronizados com sucesso.");

		return new HashSet<>(projetoIndicadorAvulsoList);

	}

	private void removerIndicadoresNaoEnviados(Projeto projeto,
			List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {

		Set<Integer> idsRecebidos = projetoIndicadorAvulsoDtoList.stream()
				.map(ProjetoIndicadorAvulsoDto::id)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<ProjetoIndicadorAvulso> indicadoresExistentes = projetoIndicadorAvulsoRepository.findAllByProjeto(projeto);

		List<ProjetoIndicadorAvulso> indicadoresParaRemover = indicadoresExistentes.stream()
				.filter(indicador -> !idsRecebidos.contains(indicador.getId()))
				.toList();

		if (!indicadoresParaRemover.isEmpty()) {
			logger.info("Removendo relacao de indicadores avulsos com projeto não enviados: {}",
					indicadoresParaRemover.size());
			projetoIndicadorAvulsoRepository.deleteAll(indicadoresParaRemover);
		}

	}

	// @Transactional
	// public Set<ProjetoIndicadorAvulso> cadastrar(Projeto projeto,
	// List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {

	// logger.info("Cadastrando indicadores avulsos do Projeto com id: {}",
	// projeto.getId());

	// Set<ProjetoIndicadorAvulso> projetoIndicadorAvulsoSet = new HashSet<>();

	// logger.info("Lista de indicadores avulsos vindos do front : {}",
	// projetoIndicadorAvulsoDtoList);

	// projetoIndicadorAvulsoDtoList.forEach(indicadorDto -> {

	// IndicadorAvulso indicadorAvulso;

	// if (indicadorDto.idIndicadorAvulso() != null) {
	// indicadorAvulso = indicadorAvulsoRepository
	// .findById(indicadorDto.idIndicadorAvulso())
	// .orElseThrow(() -> new RuntimeException("Indicador avulso não encontrado."));
	// } else {
	// indicadorAvulso = indicadorAvulsoRepository
	// .save(new IndicadorAvulso(indicadorDto));
	// }

	// ProjetoIndicadorAvulso projetoIndicadorAvulso = new ProjetoIndicadorAvulso(
	// projeto,
	// indicadorAvulso,
	// indicadorDto);

	// projetoIndicadorAvulsoSet.add(projetoIndicadorAvulso);

	// });

	// List<ProjetoIndicadorAvulso> projetoIndicadorAvulsoList =
	// projetoIndicadorAvulsoRepository
	// .saveAll(projetoIndicadorAvulsoSet);

	// logger.info("Indicadores avulsos do projeto cadastrados com sucesso.");

	// return new HashSet<>(projetoIndicadorAvulsoList);

	// }

	// @Transactional
	// public Set<ProjetoIndicador> atualizar(Projeto projeto,
	// List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {
	// logger.info("Alterando dados de indicadores avulso do Projeto com id: {}",
	// projeto.getId());

	// Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);

	// Set<ProjetoIndicador> indicadoresProjetoAtualizarSet =
	// this.atualizarIndicadoresProjeto(projeto,
	// projetoIndicadorSet, projetoIndicadorDtoList);

	// projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);

	// Set<Integer> idsDto = projetoIndicadorDtoList.stream()
	// .map(ProjetoIndicadorDto::idIndicador)
	// .filter(Objects::nonNull)
	// .collect(Collectors.toSet());

	// Set<ProjetoIndicador> indicadoresParaRemover = projetoIndicadorSet.stream()
	// .filter(indicador -> !idsDto.contains(indicador.getId()))
	// .collect(Collectors.toSet());

	// projetoIndicadorRepository.deleteAll(indicadoresParaRemover);

	// logger.info("Indicadores do projeto alterados com sucesso");

	// return this.buscarPorProjeto(projeto);
	// }

	// @Transactional
	// public void excluirPorProjeto(Projeto projeto) {
	// logger.info("Excluindo indicadores do Projeto com id: {}", projeto.getId());

	// Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);

	// List<ProjetoIndicador> projetoIndicadorList =
	// projetoIndicadorRepository.saveAllAndFlush(projetoIndicadorSet);

	// projetoIndicadorRepository.deleteAll(projetoIndicadorList);

	// logger.info("Indicadores do projeto excluídos com sucesso");
	// }

	// @Transactional
	// public void excluirFisicamentePorProjeto(Projeto projeto) {
	// logger.info("Excluindo fisicamente indicadores do Projeto com id: {}",
	// projeto.getId());

	// projetoIndicadorRepository.deleteFisicoPorProjeto(projeto.getId());

	// logger.info("Indicadores do projeto excluídos fisicamente com sucesso");
	// }

	// private Set<ProjetoIndicador> atualizarIndicadoresProjeto(Projeto projeto,
	// Set<ProjetoIndicador> indicadoresExistentes, List<ProjetoIndicadorDto>
	// dtoList) {

	// Map<Integer, ProjetoIndicador> indicadoresExistentesMap =
	// indicadoresExistentes.stream()
	// .filter(ind -> ind.getId() != null)
	// .collect(Collectors.toMap(ProjetoIndicador::getId, Function.identity()));

	// return dtoList.stream()
	// .map(dto -> {

	// Integer idIndicadorExterno = dto.idIndicadorExterno();

	// if (idIndicadorExterno == null) {
	// throw new IllegalArgumentException("Id não pode ser null");
	// }

	// ProjetoIndicador indicador;

	// if (dto.idIndicador() != null &&
	// indicadoresExistentesMap.containsKey(dto.idIndicador())) {

	// indicador = indicadoresExistentesMap.get(dto.idIndicador());
	// indicador.setId(dto.idIndicador());
	// indicador.setTipoIndicador(dto.tipoIndicador());
	// indicador.setDescricaoIndicador(dto.descricaoIndicador());
	// indicador.setDescricaoMeta(dto.descricaoMeta());
	// indicador.setTipoStatus(new TipoStatus(dto.idStatus()));

	// // indicador externo
	// if (idIndicadorExterno != null) {

	// IndicadorExterno indicadorExterno = indicadorExternoRepository
	// .findById(idIndicadorExterno)
	// .orElseThrow(() -> new RuntimeException("Indicador externo não encontrado"));

	// indicador.setIndicadorExterno(indicadorExterno);

	// // metas informadas no DTO para o indicador externo selecionado
	// if (dto.metasIndicadorProjeto() != null) {
	// atualizarMetas(indicador, dto.metasIndicadorProjeto());
	// }

	// }

	// } else {

	// IndicadorExterno indicadorExterno = indicadorExternoRepository
	// .findById(idIndicadorExterno)
	// .orElseThrow(() -> new RuntimeException("Indicador externo não encontrado"));

	// indicador = new ProjetoIndicador(projeto, dto);

	// indicador.setIndicadorExterno(indicadorExterno);

	// }

	// return indicador;

	// })
	// .collect(Collectors.toSet());

	// }

	// private void atualizarMetas(ProjetoIndicador indicador,
	// List<ProjetoIndicadorCatalogoMetaDto> metasDto) {

	// Map<Integer, ProjetoIndicadorExternoMeta> existentesMap =
	// indicador.getMetas().stream()
	// .filter(m -> m.getId() != null)
	// .collect(Collectors.toMap(ProjetoIndicadorExternoMeta::getId,
	// Function.identity()));

	// Set<ProjetoIndicadorExternoMeta> novasMetas = metasDto.stream()
	// .map(dto -> {
	// ProjetoIndicadorExternoMeta meta;

	// if (dto.idFato() != null && existentesMap.containsKey(dto.idFato())) {
	// meta = existentesMap.get(dto.idFato());
	// meta.setValorMeta(dto.valorMeta());
	// meta.setAnoMeta(dto.anoMeta());
	// } else {
	// meta = new ProjetoIndicadorExternoMeta(dto);
	// meta.setProjetoIndicador(indicador);
	// }

	// return meta;
	// })
	// .collect(Collectors.toSet());

	// indicador.getMetas().clear();
	// indicador.getMetas().addAll(novasMetas);
	// }

}