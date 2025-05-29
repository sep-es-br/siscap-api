package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoAcaoDto;
import br.gov.es.siscap.exception.ValorEstimadoIncompativelAcoesProjetoException;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoAcaoRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import br.gov.es.siscap.models.LocalidadeQuantia;
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
	public Set<ProjetoAcao> atualizar(Projeto projeto, List<ProjetoAcaoDto> ProjetoAcaoDtoList, boolean isSalvar) {
		
		logger.info("Alterando dados de acões do Projeto com id: {}", projeto.getId());

		Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);

		Set<ProjetoAcao> acoesProjetoAtualizarSet = this.atualizarAcoesProjeto( projeto, ProjetoAcaoSet, ProjetoAcaoDtoList );
		
		if(!isSalvar)
			if ( this.validarValorEstimadoProjetoAcoes( projeto, acoesProjetoAtualizarSet, isSalvar ) )
				throw new ValorEstimadoIncompativelAcoesProjetoException();

		projetoAcapRepository.saveAllAndFlush(acoesProjetoAtualizarSet);

		logger.info("Ações do projeto alterada com sucesso");

		return this.buscarPorProjeto(projeto);

	}

	private boolean validarValorEstimadoProjetoAcoes( Projeto projeto, Set<ProjetoAcao> projetoAcaoSet, boolean isSalvar ) {
						
		BigDecimal totalValorEstimadoAcoes = projetoAcaoSet.stream()
			.map(ProjetoAcao::getValorEstimado)
			.filter(Objects::nonNull)
			.collect(Collectors.reducing(
				BigDecimal.ZERO,
				BigDecimal::add
			));

			BigDecimal totalValorEstimadoProjeto = projeto.getLocalidadeQuantiaSet()
			.stream()
			.map(LocalidadeQuantia::getQuantia)
			.filter(Objects::nonNull)
			.collect(Collectors.reducing(
				BigDecimal.ZERO,
				BigDecimal::add
			));

		return totalValorEstimadoAcoes.compareTo(totalValorEstimadoProjeto) != 0;

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

		Set<ProjetoAcao> acoesAlterarSet = new HashSet<>();

		Set<ProjetoAcao> acoesAdicionarSet = new HashSet<>();

		acoesProjetoDtoList.forEach( acaoDto -> {
			acoesProjetoExistentes
						.stream()
						.filter( projetoAcao -> projetoAcao.compararIdAcaoComAcaoDto(acaoDto) )
						.findFirst()
						.ifPresentOrElse(
									(projetoAcao) -> {
										projetoAcao.atualizarAcao(acaoDto);
										acoesAlterarSet.add(projetoAcao);
									},
									() -> {
										acoesAdicionarSet.add(new ProjetoAcao(projeto, acaoDto));
									}
						);
		});

		acoesAdicionarSet.addAll(acoesAlterarSet);

		return acoesAdicionarSet;
	}

}
