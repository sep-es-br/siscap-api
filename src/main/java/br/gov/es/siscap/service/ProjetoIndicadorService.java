package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.ProjetoPessoa;
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
	private final Logger logger = LogManager.getLogger(ProjetoIndicador.class);

	public Set<ProjetoIndicador> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando indicadores do Projeto com id: {}", projeto.getId());
		return this.projetoIndicadorRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoIndicador> cadastrar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {
		logger.info("Cadastrando indicadores do Projeto com id: {}", projeto.getId());
		
		Set<ProjetoIndicador> ProjetoIndicadorSet = new HashSet<>();

		logger.info("Lista de indicadores vindas do front : {}", projetoIndicadorDtoList);
		
		projetoIndicadorDtoList.forEach( indicadorDto -> {
			ProjetoIndicador indicadorProjeto = new ProjetoIndicador(projeto, indicadorDto);
			ProjetoIndicadorSet.add(indicadorProjeto);
		});

		List<ProjetoIndicador> ProjetoIndicadorList = projetoIndicadorRepository.saveAll(ProjetoIndicadorSet);
		logger.info("Equipe do projeto cadastrada com sucesso");
		return new HashSet<>(ProjetoIndicadorList);
	}

	@Transactional
	public Set<ProjetoIndicador> atualizar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {
		logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());
		
		Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);
		
		Set<ProjetoIndicador> indicadoresProjetoAtualizarSet = this.atualizarIndicadoresProjeto( projeto, projetoIndicadorSet, projetoIndicadorDtoList );

		projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);
		
		logger.info("Indicadores do projeto alterados com sucesso");
		
		return this.buscarPorProjeto(projeto);
	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {
		logger.info("Excluindo equipe do Projeto com id: {}", projeto.getId());
		/*Set<ProjetoIndicador> ProjetoIndicadorSet = this.buscarPorProjeto(projeto);
		ProjetoIndicadorSet.forEach(ProjetoIndicador -> ProjetoIndicador.apagar("Projeto excluido"));
		List<ProjetoIndicador> ProjetoIndicadorList = ProjetoIndicadorRepository.saveAllAndFlush(ProjetoIndicadorSet);
		ProjetoIndicadorRepository.deleteAll(ProjetoIndicadorList);*/
		logger.info("Equipe do projeto excluida com sucesso");
	}
	
    private Set<ProjetoIndicador> atualizarIndicadoresProjeto( Projeto projeto,  Set<ProjetoIndicador> indicadoresExistentes, List<ProjetoIndicadorDto> dtoList) {

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
				} else {
					indicador = new ProjetoIndicador(projeto, dto);
				}
				return indicador;
			})
			.collect(Collectors.toSet());

	}


}