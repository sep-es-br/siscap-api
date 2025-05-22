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
import java.util.function.Function;
import java.util.stream.Collectors;

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
        
		logger.info("Cadastrando acoes do Projeto com id: {}", projeto.getId());

		Set<ProjetoAcao> ProjetoAcaoSet = new HashSet<>();

		ProjetoAcaoDtoList.forEach( acaoDto -> {
			ProjetoAcao acaoProjeto = new ProjetoAcao(projeto, acaoDto);
			ProjetoAcaoSet.add(acaoProjeto);
		});

		List<ProjetoAcao> ProjetoAcaoList = projetoAcapRepository.saveAll(ProjetoAcaoSet);

		logger.info("Ações do projeto cadastradas com sucesso");

		return new HashSet<>(ProjetoAcaoList);

	}

	@Transactional
	public Set<ProjetoAcao> atualizar(Projeto projeto, List<ProjetoAcaoDto> ProjetoAcaoDtoList) {
		
		logger.info("Alterando dados de acões do Projeto com id: {}", projeto.getId());

		Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);

		Set<ProjetoAcao> acoesProjetoAtualizarSet = this.atualizarAcoesProjeto( projeto, ProjetoAcaoSet, ProjetoAcaoDtoList );

		projetoAcapRepository.saveAllAndFlush(acoesProjetoAtualizarSet);

		logger.info("Ações do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto);

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo ações do Projeto com id: {}", projeto.getId());
		
		Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);
		
		List<ProjetoAcao> ProjetoAcaoList = projetoAcapRepository.saveAllAndFlush(ProjetoAcaoSet);
		
		projetoAcapRepository.deleteAll(ProjetoAcaoList);

		logger.info("Ações do projeto excluida com sucesso");

	}

	private Set<ProjetoAcao> atualizarAcoesProjeto( Projeto projeto, Set<ProjetoAcao> acoesProjetoExistentes, List<ProjetoAcaoDto> acoesProjetoDtoList ) {
			
		Map<Integer, ProjetoAcao> acoesExistentesMap = acoesProjetoExistentes.stream()
			.filter( acao -> acao.getId() != null)
			.collect( Collectors.toMap( ProjetoAcao::getId, Function.identity()) );

		return acoesProjetoDtoList.stream()
			.map( acaoDto -> {
				ProjetoAcao acao = null;
				if ( acaoDto.idAcao() != null && acoesExistentesMap.containsKey(acaoDto.idAcao()) ) {
					acao = acoesExistentesMap.get(acaoDto.idAcao());
					acao.setId(acaoDto.idAcao());
					acao.setDescricaoAcaoPrincipal(acaoDto.descricaoAcaoPrincipal());
					acao.setDescricaoAcaoSecundaria(acaoDto.descricaoAcaoSecundaria());
					acao.setValorEstimado(acaoDto.valorEstimadoAcaoPrincipal());
				} else {
					acao = new ProjetoAcao(projeto, acaoDto);	
				}
				return acao;
			})
			.collect(Collectors.toSet());

	}

}
