package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorCatalogoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.models.IndicadorExterno;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;
import br.gov.es.siscap.models.TipoStatus;
import br.gov.es.siscap.repository.IndicadorExternoRepository;
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
	private final IndicadorExternoRepository indicadorExternoRepository;
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
			if (indicadorDto.idIndicadorExterno() != null) {
				IndicadorExterno indicador = indicadorExternoRepository
						.findById(indicadorDto.idIndicadorExterno())
						.orElseThrow(() -> new RuntimeException("Indicador externo não encontrado"));
				indicadorProjeto.setIndicadorExterno(indicador);
			}
			projetoIndicadorSet.add(indicadorProjeto);
		});

		List<ProjetoIndicador> projetoIndicadorList = projetoIndicadorRepository.saveAll(projetoIndicadorSet);

		logger.info("Equipe do projeto cadastrada com sucesso");

		return new HashSet<>(projetoIndicadorList);

	}

	@Transactional
	public Set<ProjetoIndicador> atualizar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {
		logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());

		Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);

		Set<ProjetoIndicador> indicadoresProjetoAtualizarSet = this.atualizarIndicadoresProjeto(projeto,
				projetoIndicadorSet, projetoIndicadorDtoList);

		projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);

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

		projetoIndicadorRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Indicadores do projeto excluídos fisicamente com sucesso");
	}

	private Set<ProjetoIndicador> atualizarIndicadoresProjeto(Projeto projeto,
			Set<ProjetoIndicador> indicadoresExistentes, List<ProjetoIndicadorDto> dtoList) {

		Map<Integer, ProjetoIndicador> indicadoresExistentesMap = indicadoresExistentes.stream()
				.filter(ind -> ind.getId() != null)
				.collect(Collectors.toMap(ProjetoIndicador::getId, Function.identity()));

		return dtoList.stream()
				.map(dto -> {
					ProjetoIndicador indicador;
					if (dto.idIndicador() != null && indicadoresExistentesMap.containsKey(dto.idIndicador())) {

						indicador = indicadoresExistentesMap.get(dto.idIndicador());
						indicador.setId(dto.idIndicador());
						indicador.setTipoIndicador(dto.tipoIndicador());
						indicador.setDescricaoIndicador(dto.descricaoIndicador());
						indicador.setDescricaoMeta(dto.descricaoMeta());
						indicador.setTipoStatus(new TipoStatus(dto.idStatus()));

						// indicador externo
						if (dto.idIndicadorExterno() != null) {

							IndicadorExterno indicadorExterno = indicadorExternoRepository
									.findById(dto.idIndicadorExterno())
									.orElseThrow(() -> new RuntimeException("Indicador externo não encontrado"));

							indicador.setIndicadorExterno(indicadorExterno);

							// metas informadas no DTO para o indicador externo selecionado
							if (dto.metas() != null) {
								atualizarMetas(indicador, dto.metas());
							}

						} else {
							indicador.setIndicadorExterno(null);
						}

					} else {
						indicador = new ProjetoIndicador(projeto, dto);
					}
					
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