package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoAcaoDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoAcaoRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import br.gov.es.siscap.models.ProjetoAcao;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoAcaoService {

	private final ProjetoAcaoRepository projetoAcapRepository;
	private final Logger logger = LogManager.getLogger(ProjetoAcao.class);

	public Set<ProjetoAcao> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando acoes do Projeto com id: {}", projeto.getId());
		return this.projetoAcapRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoAcao> cadastrar(Projeto projeto, List<ProjetoAcaoDto> ProjetoAcaoDtoList) {
		/*
        logger.info("Cadastrando acoes do Projeto com id: {}", projeto.getId());
		Set<ProjetoAcao> ProjetoAcaoSet = new HashSet<>();
		ProjetoAcaoDtoList.forEach( acaoDto -> {
			ProjetoAcao acaoProjeto = new ProjetoAcao(projeto, acaoDto);
			ProjetoAcaoSet.add(acaoProjeto);
		});
		List<ProjetoAcao> ProjetoAcaoList = projetoAcapRepository.saveAll(ProjetoAcaoSet);
		logger.info("Ações do projeto cadastradas com sucesso");
		return new HashSet<>(ProjetoAcaoList);
        */
        return null;
	}

	@Transactional
	public Set<ProjetoAcao> atualizar(Projeto projeto, List<ProjetoAcaoDto> ProjetoAcaoDtoList) {
		logger.info("Alterando dados de indicadores do Projeto com id: {}", projeto.getId());
		
		/*Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);
		ProjetoAcao responsavelProponente = this.buscarResponsavelProponente(ProjetoAcaoSet);
		if (!this.compararIdsResponsavelProponente(responsavelProponente.getPessoa().getId(), idResponsavelProponente)) {
			responsavelProponente.atualizarResponsavelProponente(TipoStatusEnum.INATIVO.getValue());
			projetoAcapRepository.save(responsavelProponente);
			projetoAcapRepository.save(new ProjetoAcao(projeto, idResponsavelProponente));
		}
		Set<ProjetoAcao> membrosEquipeSet = this.buscarMembrosEquipe(ProjetoAcaoSet);
		Set<ProjetoAcao> membrosEquipeAtualizarSet = this.atualizarMembrosEquipe(projeto, membrosEquipeSet, ProjetoAcaoDtoList);
		projetoAcapRepository.saveAllAndFlush(membrosEquipeAtualizarSet);*/
		logger.info("Equipe do projeto alterada com sucesso");
		return this.buscarPorProjeto(projeto);
	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {
		logger.info("Excluindo equipe do Projeto com id: {}", projeto.getId());
		/*Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);
		ProjetoAcaoSet.forEach(ProjetoAcao -> ProjetoAcao.apagar("Projeto excluido"));
		List<ProjetoAcao> ProjetoAcaoList = projetoAcapRepository.saveAllAndFlush(ProjetoAcaoSet);
		projetoAcapRepository.deleteAll(ProjetoAcaoList);*/
		logger.info("Equipe do projeto excluida com sucesso");
	}

}
