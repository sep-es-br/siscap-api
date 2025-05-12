package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.repository.ProjetoIndicadorRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
	public Set<ProjetoIndicador> cadastrar(Projeto projeto, List<ProjetoIndicadorDto> ProjetoIndicadorDtoList) {
		logger.info("Cadastrando indicadores do Projeto com id: {}", projeto.getId());
		Set<ProjetoIndicador> ProjetoIndicadorSet = new HashSet<>();
		/*
		ProjetoIndicador responsavelProponente = new ProjetoIndicador(projeto, idResponsavelProponente);
		ProjetoIndicadorSet.add(responsavelProponente);
		ProjetoIndicadorDtoList.forEach(ProjetoIndicadorDto -> {
			ProjetoIndicador ProjetoIndicador = new ProjetoIndicador(projeto, ProjetoIndicadorDto);
			ProjetoIndicadorSet.add(ProjetoIndicador);
		});*/
		List<ProjetoIndicador> ProjetoIndicadorList = projetoIndicadorRepository.saveAll(ProjetoIndicadorSet);
		logger.info("Equipe do projeto cadastrada com sucesso");
		return new HashSet<>(ProjetoIndicadorList);
	}

	@Transactional
	public Set<ProjetoIndicador> atualizar(Projeto projeto, List<ProjetoIndicadorDto> ProjetoIndicadorDtoList) {
		logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());
		/*Set<ProjetoIndicador> ProjetoIndicadorSet = this.buscarPorProjeto(projeto);
		ProjetoIndicador responsavelProponente = this.buscarResponsavelProponente(ProjetoIndicadorSet);
		if (!this.compararIdsResponsavelProponente(responsavelProponente.getPessoa().getId(), idResponsavelProponente)) {
			responsavelProponente.atualizarResponsavelProponente(TipoStatusEnum.INATIVO.getValue());
			ProjetoIndicadorRepository.save(responsavelProponente);
			ProjetoIndicadorRepository.save(new ProjetoIndicador(projeto, idResponsavelProponente));
		}
		Set<ProjetoIndicador> membrosEquipeSet = this.buscarMembrosEquipe(ProjetoIndicadorSet);
		Set<ProjetoIndicador> membrosEquipeAtualizarSet = this.atualizarMembrosEquipe(projeto, membrosEquipeSet, ProjetoIndicadorDtoList);
		ProjetoIndicadorRepository.saveAllAndFlush(membrosEquipeAtualizarSet);*/
		logger.info("Equipe do projeto alterada com sucesso");
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

}