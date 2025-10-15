package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoParecer;
import br.gov.es.siscap.repository.ProjetoParecerRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoParecerService {

	private final ProjetoParecerRepository projetoParecerRepository;
	private final Logger logger = LogManager.getLogger(ProjetoParecer.class);

	public Set<ProjetoParecer> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando pareceres vinculados ao DIC com id: {}", projeto.getId());
		return this.projetoParecerRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoParecer> cadastrar(Projeto projeto, List<ProjetoParecerDto> projetoPareceresDtoList) {
		
		logger.info("Cadastrando pareceres DIC com id: {}", projeto.getId());
		
		Set<ProjetoParecer> ProjetoComplementosSet = new HashSet<>();
		
		projetoPareceresDtoList.forEach( parecerDto -> {
			ProjetoParecer complementoProjeto = new ProjetoParecer(projeto, parecerDto);
			ProjetoComplementosSet.add(complementoProjeto);
		});

		List<ProjetoParecer> ProjetoComplementoList = projetoParecerRepository.saveAll(ProjetoComplementosSet);

		logger.info("Campos a serem complementados para o DIC cadastrada com sucesso");

		return new HashSet<>(ProjetoComplementoList);

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo pareceres por DIC com id: {}", projeto.getId());
		
		Set<ProjetoParecer> projetoIndicadorSet = this.buscarPorProjeto(projeto);
		
		projetoParecerRepository.deleteAll(projetoIndicadorSet);
		
		logger.info(" pareceres vinculados ao DIC excluídos com sucesso" );

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {

		logger.info("Excluindo fisicamente pareceres registrados do DIC com id: {}", projeto.getId());

		projetoParecerRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Ações do projeto excluidas fisicamente com sucesso");

	}

}