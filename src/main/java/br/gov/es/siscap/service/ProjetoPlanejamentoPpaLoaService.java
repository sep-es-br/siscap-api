package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoPlanejamentoPpaLoaDto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPlanejamentoPpaLoa;
import br.gov.es.siscap.repository.ProjetoPlanejamentoPpaLoaRepository;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoPlanejamentoPpaLoaService {

	private final ProjetoPlanejamentoPpaLoaRepository projetoPlanejamentoPpaLoaRepository;

	private final Logger logger = LogManager.getLogger(ProjetoPlanejamentoPpaLoaService.class);

	public Set<ProjetoPlanejamentoPpaLoa> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando planejamento PPA LOA do Projeto com id: {}", projeto.getId());
		return this.projetoPlanejamentoPpaLoaRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoPlanejamentoPpaLoa> sincronizar(
			Projeto projeto,
			List<ProjetoPlanejamentoPpaLoaDto> projetoPlanejamentoPpaLoaDtoList) {

		logger.info("Sincronizando planejamento PPA LOA do Projeto com id: {}", projeto.getId());

		List<ProjetoPlanejamentoPpaLoaDto> planejamentos = projetoPlanejamentoPpaLoaDtoList != null
				? projetoPlanejamentoPpaLoaDtoList
				: List.of();

		removerPlanejamentosNaoEnviados(projeto, planejamentos);

		Set<ProjetoPlanejamentoPpaLoa> projetoPlanejamentoPpaLoaSet = planejamentos.stream()
				.map(dto -> buscarOuCriarProjetoPlanejamentoPpaLoa(projeto, dto))
				.collect(Collectors.toSet());

		List<ProjetoPlanejamentoPpaLoa> projetoPlanejamentoPpaLoaList = projetoPlanejamentoPpaLoaRepository.saveAll(
				projetoPlanejamentoPpaLoaSet);

		logger.info("Planejamentos PPA LOA sincronizados com sucesso.");

		return new HashSet<>(projetoPlanejamentoPpaLoaList);

	}

	private void removerPlanejamentosNaoEnviados(Projeto projeto,
			List<ProjetoPlanejamentoPpaLoaDto> projetoPlanejamentoPpaLoaDtoList) {

		Set<Long> idsRecebidos = projetoPlanejamentoPpaLoaDtoList.stream()
				.map(ProjetoPlanejamentoPpaLoaDto::id)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<ProjetoPlanejamentoPpaLoa> planejamentosExistentes = projetoPlanejamentoPpaLoaRepository
				.findAllByProjeto(projeto);

		List<ProjetoPlanejamentoPpaLoa> planejamentosParaRemover = planejamentosExistentes.stream()
				.filter(planejamento -> !idsRecebidos.contains(planejamento.getId()))
				.toList();

		if (!planejamentosParaRemover.isEmpty()) {
			logger.info("Removendo relacao de planejamentos PPA LOA com projeto não enviados: {}",
					planejamentosParaRemover.size());

			projetoPlanejamentoPpaLoaRepository.deleteAll(planejamentosParaRemover);

			projetoPlanejamentoPpaLoaRepository.flush();
		}

	}

	private ProjetoPlanejamentoPpaLoa buscarOuCriarProjetoPlanejamentoPpaLoa(
			Projeto projeto,
			ProjetoPlanejamentoPpaLoaDto planejamentoDto) {

		if (planejamentoDto.id() != null) {

			return projetoPlanejamentoPpaLoaRepository
					.findById(planejamentoDto.id())
					.orElseThrow(() -> new RuntimeException(
							"Planejamento PPA LOA não encontrado."));
		}

		return new ProjetoPlanejamentoPpaLoa(
				projeto,
				planejamentoDto);

	}

	@Transactional
	public Set<ProjetoPlanejamentoPpaLoa> atualizar(Projeto projeto,
			Set<ProjetoPlanejamentoPpaLoa> acoesProjetoExistentes,
			List<ProjetoPlanejamentoPpaLoaDto> projetoPlanejamentoPpaLoaDtoList) {

		logger.info("Alterando dados de Planejamentos PPA LOA do Projeto com id: {}", projeto.getId());

		Set<ProjetoPlanejamentoPpaLoa> acoesAlterarSet = new HashSet<>();

		Set<ProjetoPlanejamentoPpaLoa> acoesPersistirSet = new HashSet<>();

		projetoPlanejamentoPpaLoaDtoList.forEach(acaoDto -> {
			acoesProjetoExistentes
					.stream()
					.filter(projetoAcao -> projetoAcao.compararIdAcaoComAcaoDto(acaoDto))
					.findFirst()
					.ifPresentOrElse(
							(projetoAcao) -> {
								projetoAcao.atualizarAcao(acaoDto);
								acoesAlterarSet.add(projetoAcao);
							},
							() -> acoesPersistirSet.add(new ProjetoPlanejamentoPpaLoa(projeto, acaoDto)));
		});

		acoesPersistirSet.addAll(acoesAlterarSet);

		projetoPlanejamentoPpaLoaRepository.saveAllAndFlush(acoesPersistirSet);

		Set<ProjetoPlanejamentoPpaLoa> acoesRemover = acoesProjetoExistentes.stream()
				.filter(acaoExistente -> projetoPlanejamentoPpaLoaDtoList.stream()
						.noneMatch(acaoDto -> acaoExistente
								.compararIdAcaoComAcaoDto(
										acaoDto)))
				.collect(Collectors.toSet());

		if (!acoesRemover.isEmpty()) {
			projetoPlanejamentoPpaLoaRepository.deleteAll(acoesRemover);
		}

		projetoPlanejamentoPpaLoaRepository.flush();

		return acoesPersistirSet;

	}

	@Transactional
	public void excluirFisicamentePorProjeto(Projeto projeto) {

		logger.info("Excluindo fisicamente ações de planejamento do Projeto com id: {}", projeto.getId());

		projetoPlanejamentoPpaLoaRepository.deleteFisicoPorProjeto(projeto.getId());

		logger.info("Ações de planejamento do projeto excluidas fisicamente com sucesso");

	}

}