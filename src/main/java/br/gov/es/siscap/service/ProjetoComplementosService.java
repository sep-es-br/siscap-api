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

}