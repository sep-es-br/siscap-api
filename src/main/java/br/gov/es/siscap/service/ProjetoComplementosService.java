package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoCamposComplementacao;
import br.gov.es.siscap.repository.ProjetoComplementosRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoComplementosService {

	private final ProjetoComplementosRepository projetoComplementosRepository;
	private final Logger logger = LogManager.getLogger(ProjetoCamposComplementacao.class);

	public Set<ProjetoCamposComplementacao> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando campos a serem complementados do DIC com id: {}", projeto.getId());
		return this.projetoComplementosRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoCamposComplementacao> cadastrar(Projeto projeto, List<ProjetoCamposComplementacaoDto> projetoComplementosDtoList) {
		
		logger.info("Cadastrando campos a serem complementados do DIC com id: {}", projeto.getId());
		
		Set<ProjetoCamposComplementacao> ProjetoComplementosSet = new HashSet<>();
		
		projetoComplementosDtoList.forEach( complementoDto -> {
			ProjetoCamposComplementacao complementoProjeto = new ProjetoCamposComplementacao(projeto, complementoDto);
			ProjetoComplementosSet.add(complementoProjeto);
		});

		List<ProjetoCamposComplementacao> ProjetoComplementoList = projetoComplementosRepository.saveAll(ProjetoComplementosSet);

		logger.info("Campos a serem complementados para o DIC cadastrada com sucesso");

		return new HashSet<>(ProjetoComplementoList);

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo campos a serem complementados do DIC com id: {}", projeto.getId());
		
		Set<ProjetoCamposComplementacao> projetoIndicadorSet = this.buscarPorProjeto(projeto);
		
		projetoComplementosRepository.deleteAll(projetoIndicadorSet);
		
		logger.info(" campos a serem complementados do DIC excluídos com sucesso" );

	}

	// @Transactional
	// public Set<ProjetoIndicador> atualizar(Projeto projeto, List<ProjetoIndicadorDto> projetoIndicadorDtoList) {
	// 	logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());
	// 	Set<ProjetoIndicador> projetoIndicadorSet = this.buscarPorProjeto(projeto);
	// 	Set<ProjetoIndicador> indicadoresProjetoAtualizarSet = this.atualizarIndicadoresProjeto( projeto, projetoIndicadorSet, projetoIndicadorDtoList );
	// 	projetoIndicadorRepository.saveAllAndFlush(indicadoresProjetoAtualizarSet);
	// 	logger.info("Indicadores do projeto alterados com sucesso");
	// 	return this.buscarPorProjeto(projeto);
	// }

	
	
    // private Set<ProjetoIndicador> atualizarIndicadoresProjeto( Projeto projeto,  Set<ProjetoIndicador> indicadoresExistentes, List<ProjetoIndicadorDto> dtoList) {
	// 	Map<Integer, ProjetoIndicador> indicadoresExistentesMap = indicadoresExistentes.stream()
	// 		.filter(ind -> ind.getId() != null)
	// 		.collect(Collectors.toMap(ProjetoIndicador::getId, Function.identity()));
	// 	return dtoList.stream()
	// 		.map(dto -> {
	// 			ProjetoIndicador indicador;
	// 			if (dto.idIndicador() != null && indicadoresExistentesMap.containsKey(dto.idIndicador())) {
	// 				indicador = indicadoresExistentesMap.get(dto.idIndicador());
	// 				indicador.setId(dto.idIndicador());
	// 				indicador.setTipoIndicador(dto.tipoIndicador());
	// 				indicador.setDescricaoIndicador(dto.descricaoIndicador());
	// 				indicador.setDescricaoMeta(dto.descricaoMeta());
	// 				indicador.setTipoStatus( new TipoStatus(dto.idStatus()) );
	// 			} else {
	// 				indicador = new ProjetoIndicador(projeto, dto);
	// 			}
	// 			return indicador;
	// 		})
	// 		.collect(Collectors.toSet());
	// }


}