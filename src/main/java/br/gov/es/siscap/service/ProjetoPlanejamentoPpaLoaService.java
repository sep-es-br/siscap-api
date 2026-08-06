package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import br.gov.es.siscap.dto.ProjetoPlanejamentoPpaLoaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.models.IndicadorAvulso;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoIndicadorAvulso;
import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;
import br.gov.es.siscap.models.ProjetoOds;
import br.gov.es.siscap.models.ProjetoPlanejamentoPpaLoa;
import br.gov.es.siscap.repository.IndicadorAvulsoRepository;
import br.gov.es.siscap.repository.ProjetoIndicadorAvulsoMetaRepository;
import br.gov.es.siscap.repository.ProjetoIndicadorAvulsoRepository;
import br.gov.es.siscap.repository.ProjetoPlanejamentoPpaLoaRepository;
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
public class ProjetoPlanejamentoPpaLoaService {

	private final ProjetoPlanejamentoPpaLoaRepository projetoPlanejamentoPpaLoaRepository;

	private final Logger logger = LogManager.getLogger(ProjetoPlanejamentoPpaLoaService.class);

	public Set<ProjetoPlanejamentoPpaLoa> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando planejamento PPA LOA do Projeto com id: {}", projeto.getId());
		return this.projetoPlanejamentoPpaLoaRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoPlanejamentoPpaLoa> sincronizar(Projeto projeto,
			List<ProjetoPlanejamentoPpaLoaDto> projetoPlanejamentoPpaLoaDtoList) {

		logger.info("Sincronizando planejamento PPA LOA do Projeto com id: {}", projeto.getId());

		removerPlanejamentosNaoEnviados(projeto, projetoPlanejamentoPpaLoaDtoList);

		Set<ProjetoPlanejamentoPpaLoa> projetoPlanejamentoPpaLoaSet = new HashSet<>();

		projetoPlanejamentoPpaLoaDtoList.forEach(planejamentoDto -> {

			ProjetoPlanejamentoPpaLoa projetoPlanejamentoPpaLoa;

			Long idPlanejamento = planejamentoDto.id();

			if (idPlanejamento != null) {

				projetoPlanejamentoPpaLoa = projetoPlanejamentoPpaLoaRepository
						.findById(idPlanejamento)
						.orElseThrow(() -> new RuntimeException("Planejamento PPA LOA não encontrado."));

			} else {

				projetoPlanejamentoPpaLoa = projetoPlanejamentoPpaLoaRepository
						.save(new ProjetoPlanejamentoPpaLoa(planejamentoDto));

			}

			if (projetoPlanejamentoPpaLoa != null)
				projetoPlanejamentoPpaLoa = projetoPlanejamentoPpaLoaRepository.save(projetoPlanejamentoPpaLoa);
			else
				throw new SiscapServiceException(
						Arrays.asList("Planejamento PPA LOA inválido."));

			projetoPlanejamentoPpaLoa = buscarOuCriarProjetoPlanejamentoPpaLoa(projeto, planejamentoDto);

			projetoPlanejamentoPpaLoaSet.add(projetoPlanejamentoPpaLoa);

		});

		List<ProjetoPlanejamentoPpaLoa> projetoPlanejamentoPpaLoaList = projetoPlanejamentoPpaLoaRepository
				.saveAll(projetoPlanejamentoPpaLoaSet);

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
		}

	}

	private ProjetoPlanejamentoPpaLoa buscarOuCriarProjetoPlanejamentoPpaLoa(
			Projeto projeto,
			ProjetoPlanejamentoPpaLoaDto planejamentoDto) {

		if (planejamentoDto.id() != null) {
			return projetoPlanejamentoPpaLoaRepository
					.findById(planejamentoDto.id())
					.orElseThrow(() -> new RuntimeException("Planejamento PPA LOA não encontrado."));
		}

		ProjetoPlanejamentoPpaLoa novo = new ProjetoPlanejamentoPpaLoa(projeto, planejamentoDto);

		return projetoPlanejamentoPpaLoaRepository.save(novo);

	}

	@Transactional
	public Set<ProjetoPlanejamentoPpaLoa> atualizar(Projeto projeto,
			Set<ProjetoPlanejamentoPpaLoa> acoesProjetoExistentes,
			List<ProjetoPlanejamentoPpaLoaDto> projetoPlanejamentoPpaLoaDtoList) {

		logger.info("Alterando dados de Planejamentos PPA LOA do Projeto com id: {}", projeto.getId());

		Set<ProjetoPlanejamentoPpaLoa> acoesAlterarSet = new HashSet<>();

		Set<ProjetoPlanejamentoPpaLoa> acoesAdicionarSet = new HashSet<>();

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
							() -> acoesAdicionarSet.add(new ProjetoPlanejamentoPpaLoa(projeto, acaoDto)));
		});

		acoesAdicionarSet.addAll(acoesAlterarSet);

		projetoPlanejamentoPpaLoaRepository.saveAllAndFlush(acoesAdicionarSet);

		Set<ProjetoPlanejamentoPpaLoa> acoesRemover = acoesProjetoExistentes.stream()
				.filter(acaoExistente -> projetoPlanejamentoPpaLoaDtoList.stream()
						.noneMatch(acaoDto -> acaoExistente
								.compararIdAcaoComAcaoDto(
										acaoDto)))
				.collect(Collectors.toSet());

		if (!acoesRemover.isEmpty())
			projetoPlanejamentoPpaLoaRepository.deleteAll(acoesRemover);

		return acoesAdicionarSet;
		
	}

	// private Set<ProjetoPlanejamentoPpaLoa> atualizarPlanejamentoPpaLoaProjeto(
	// Projeto projeto,
	// Set<ProjetoPlanejamentoPpaLoa> planejamentosExistentes,
	// List<ProjetoPlanejamentoPpaLoaDto> dtoList) {
	// Map<Long, ProjetoPlanejamentoPpaLoa> planejamentosExistentesMap =
	// planejamentosExistentes.stream()
	// .filter(planejamento -> planejamento.getId() != null)
	// .collect(Collectors.toMap(ProjetoPlanejamentoPpaLoa::getId,
	// Function.identity()));
	// return dtoList.stream()
	// .map(dto -> {
	// if (dto.id() == null) {
	// throw new ValidacaoSiscapException(List.of("Id do Planejamento PPA LOA não
	// pode ser null."));
	// }
	// ProjetoPlanejamentoPpaLoa planejamento;
	// if (dto.id() != null && planejamentosExistentesMap.containsKey(dto.id())) {
	// planejamento = planejamentosExistentesMap.get(dto.id());
	// } else {
	// planejamento = new ProjetoPlanejamentoPpaLoa();
	// planejamento.setProjeto(projeto);
	// }
	// planejamento.setId(dto.id());
	// planejamento.setCodFuncao(dto.codFuncao());
	// planejamento.setCodPrograma(dto.codPrograma());
	// planejamento.setAno(dto.ano());
	// planejamento.setCodUo(dto.codUo());
	// planejamento.setCodAcao(dto.codAcao());
	// return planejamento;
	// })
	// .collect(Collectors.toSet());
	// }

	// @Transactional
	// public void excluirFisicamentePorProjeto(Projeto projeto) {
	// logger.info("Excluindo fisicamente indicadores avulsos do Projeto com id:
	// {}", projeto.getId());
	// projetoIndicadorAvulsoMetaRepository.deleteFisicoPorProjeto(projeto.getId());
	// projetoIndicadorAvulsoRepository.deleteFisicoPorProjeto(projeto.getId());
	// logger.info("Indicadores avulsos do projeto excluídos fisicamente com
	// sucesso");
	// }

}