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
import java.util.function.Function;
import java.util.stream.Collectors;

import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.ProjetoAcao;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoAcaoService {

	private final ProjetoAcaoRepository projetoAcaoRepository;
	private final ProjetoAcaoLocalidadeQuantiaService projetoAcaoLocalidadeQuantiaService;
	private final Logger logger = LogManager.getLogger(ProjetoAcaoService.class);

	public Set<ProjetoAcao> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando acoes do Projeto com id: {}", projeto.getId());
		return this.projetoAcaoRepository.findAllByProjeto(projeto);
	}

	// @Transactional
	// public Set<ProjetoAcao> cadastrar(Projeto projeto, List<ProjetoAcaoDto>
	// ProjetoAcaoDtoList) {
	// logger.info("Cadastrando acoes do Projeto com id: {}", projeto.getId());
	// Set<ProjetoAcao> ProjetoAcaoSet = new HashSet<>();
	// ProjetoAcaoDtoList.forEach(acaoDto -> {
	// ProjetoAcao acaoProjeto = new ProjetoAcao(projeto, acaoDto);
	// ProjetoAcaoSet.add(acaoProjeto);
	// });
	// List<ProjetoAcao> ProjetoAcaoList =
	// projetoAcapRepository.saveAll(ProjetoAcaoSet);
	// logger.info("Ações do projeto cadastradas com sucesso");
	// return new HashSet<>(ProjetoAcaoList);
	// }

	@Transactional
	public Set<ProjetoAcao> cadastrar(
			Projeto projeto,
			List<ProjetoAcaoDto> projetoAcaoDtoList) {

		logger.info(
				"Cadastrando ações do Projeto com id: {}",
				projeto.getId());

		if (projetoAcaoDtoList == null || projetoAcaoDtoList.isEmpty()) {
			logger.info(
					"Nenhuma ação informada para o Projeto com id: {}",
					projeto.getId());

			return Set.of();
		}

		Set<ProjetoAcao> projetoAcaoSet = new HashSet<>();

		projetoAcaoDtoList.forEach(acaoDto -> {

			ProjetoAcao projetoAcao = projetoAcaoRepository.save(
					new ProjetoAcao(projeto, acaoDto));

			projetoAcaoLocalidadeQuantiaService.cadastrar(
					projetoAcao,
					acaoDto.rateio());

			projetoAcaoSet.add(projetoAcao);
		});

		logger.info(
				"Ações e rateios do Projeto com id {} cadastrados com sucesso",
				projeto.getId());

		return projetoAcaoSet;
	}

	// @Transactional
	// public Set<ProjetoAcao> atualizar(Projeto projeto, List<ProjetoAcaoDto>
	// ProjetoAcaoDtoList, boolean isSalvar) {
	// logger.info("Alterando dados de acões do Projeto com id: {}",
	// projeto.getId());
	// Set<ProjetoAcao> ProjetoAcaoSet = this.buscarPorProjeto(projeto);
	// Set<ProjetoAcao> acoesProjetoAtualizarSet =
	// this.atualizarAcoesProjeto(projeto, ProjetoAcaoSet,
	// ProjetoAcaoDtoList);
	// if (!isSalvar)
	// if (this.validarValorEstimadoProjetoAcoes(projeto, acoesProjetoAtualizarSet,
	// isSalvar))
	// throw new ValorEstimadoIncompativelAcoesProjetoException();
	// projetoAcaoRepository.saveAllAndFlush(acoesProjetoAtualizarSet);
	// logger.info("Ações do projeto alterada com sucesso");
	// return this.buscarPorProjeto(projeto);
	// }

	@Transactional
	public Set<ProjetoAcao> atualizar(
			Projeto projeto,
			List<ProjetoAcaoDto> acoesDto,
			boolean isSalvar) {

		logger.info("Alterando ações do Projeto com id: {}", projeto.getId());

		Set<ProjetoAcao> acoesAtuais = buscarPorProjeto(projeto);

		Map<Integer, ProjetoAcao> acoesPorId = acoesAtuais.stream()
				.filter(acao -> acao.getId() != null)
				.collect(Collectors.toMap(
						ProjetoAcao::getId,
						Function.identity()));

		Map<ProjetoAcaoDto, ProjetoAcao> acoesProcessadas = new LinkedHashMap<>();

		acoesDto.forEach(dto -> {

			ProjetoAcao acao = Optional.ofNullable(dto.idAcao())
					.filter(id -> id > 0)
					.map(acoesPorId::get)
					.orElseGet(() -> new ProjetoAcao(projeto, dto));

			if (acao.getId() != null) {
				acao.atualizarAcao(dto);
			}

			acoesProcessadas.put(dto, acao);
			
		});

		Set<ProjetoAcao> acoesAtualizadas = new HashSet<>(acoesProcessadas.values());

		if (!isSalvar &&
				validarValorEstimadoProjetoAcoes(
						projeto,
						acoesAtualizadas,
						false)) {

			throw new ValorEstimadoIncompativelAcoesProjetoException();
		}

		projetoAcaoRepository.saveAllAndFlush(acoesAtualizadas);

		/*
		 * Neste ponto as ações novas já possuem ID.
		 * Agora sincronizamos o rateio de cada ação.
		 */
		acoesProcessadas.forEach((dto, acao) -> projetoAcaoLocalidadeQuantiaService.atualizar(
				acao,
				dto.rateio()));

		logger.info(
				"Ações e rateios do Projeto com id {} alterados com sucesso",
				projeto.getId());

		return buscarPorProjeto(projeto);
	}

	private boolean validarValorEstimadoProjetoAcoes(Projeto projeto, Set<ProjetoAcao> projetoAcaoSet,
			boolean isSalvar) {

		BigDecimal totalValorEstimadoAcoes = projetoAcaoSet.stream()
				.map(ProjetoAcao::getValorEstimado)
				.filter(Objects::nonNull)
				.collect(Collectors.reducing(
						BigDecimal.ZERO,
						BigDecimal::add));

		BigDecimal totalValorEstimadoProjeto = projeto.getLocalidadeQuantiaSet()
				.stream()
				.map(LocalidadeQuantia::getQuantia)
				.filter(Objects::nonNull)
				.collect(Collectors.reducing(
						BigDecimal.ZERO,
						BigDecimal::add));

		return totalValorEstimadoAcoes.compareTo(totalValorEstimadoProjeto) != 0;

	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {

		logger.info("Excluindo ações do Projeto com id: {}", projeto.getId());

		Set<ProjetoAcao> projetoAcaoSet = this.buscarPorProjeto(projeto);

		if (projetoAcaoSet.isEmpty()) {
			logger.info("Nenhuma ação de projeto encontrada para exclusão.");
			return;
		}

		List<ProjetoAcao> projetoAcaoList = projetoAcaoRepository.saveAllAndFlush(projetoAcaoSet);

		projetoAcaoRepository.deleteAll(projetoAcaoList);

		logger.info("Ações do projeto excluida com sucesso");

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {

		logger.info("Excluindo fisicamente ações do Projeto com id: {}", projeto.getId());

		projetoAcaoRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Ações do projeto excluidas fisicamente com sucesso");

	}

	private Set<ProjetoAcao> atualizarAcoesProjeto(Projeto projeto, Set<ProjetoAcao> acoesProjetoExistentes,
			List<ProjetoAcaoDto> acoesProjetoDtoList) {

		Set<ProjetoAcao> acoesAlterarSet = new HashSet<>();

		Set<ProjetoAcao> acoesAdicionarSet = new HashSet<>();

		acoesProjetoDtoList.forEach(acaoDto -> {
			acoesProjetoExistentes
					.stream()
					.filter(projetoAcao -> projetoAcao.compararIdAcaoComAcaoDto(acaoDto))
					.findFirst()
					.ifPresentOrElse(
							(projetoAcao) -> {
								projetoAcao.atualizarAcao(acaoDto);
								acoesAlterarSet.add(projetoAcao);
							},
							() -> {
								acoesAdicionarSet.add(new ProjetoAcao(projeto, acaoDto));
							});
		});

		acoesAdicionarSet.addAll(acoesAlterarSet);

		return acoesAdicionarSet;
	}

}
