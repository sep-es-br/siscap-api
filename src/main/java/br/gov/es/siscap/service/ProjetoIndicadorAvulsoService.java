package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.IndicadorAvulsoMetaDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoMetaDto;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.models.IndicadorAvulso;
import br.gov.es.siscap.models.IndicadorAvulsoMeta;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoIndicadorAvulso;
import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;
import br.gov.es.siscap.repository.IndicadorAvulsoMetaRepository;
import br.gov.es.siscap.repository.IndicadorAvulsoRepository;
import br.gov.es.siscap.repository.ProjetoIndicadorAvulsoMetaRepository;
import br.gov.es.siscap.repository.ProjetoIndicadorAvulsoRepository;
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
public class ProjetoIndicadorAvulsoService {

	private final ProjetoIndicadorAvulsoRepository projetoIndicadorAvulsoRepository;
	private final IndicadorAvulsoRepository indicadorAvulsoRepository;
	private final ProjetoIndicadorAvulsoMetaRepository projetoIndicadorAvulsoMetaRepository;
	private final IndicadorAvulsoMetaRepository indicadorAvulsoMetaRepository;

	private final Logger logger = LogManager.getLogger(ProjetoIndicadorAvulsoService.class);

	public Set<ProjetoIndicadorAvulso> buscarPorProjeto(Projeto projeto) {
		logger.info("Buscando indicadores avulsos do Projeto com id: {}", projeto.getId());
		return this.projetoIndicadorAvulsoRepository.findAllByProjeto(projeto);
	}

	@Transactional
	public Set<ProjetoIndicadorAvulso> sincronizar(Projeto projeto,
			List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {

		logger.info("Sincronizando indicadores avulsos do Projeto com id: {}", projeto.getId());
		logger.info("Lista de indicadores avulsos vindos do front: {}", projetoIndicadorAvulsoDtoList);

		removerIndicadoresNaoEnviados(projeto, projetoIndicadorAvulsoDtoList);

		Set<ProjetoIndicadorAvulso> projetoIndicadorAvulsoSet = new HashSet<>();

		projetoIndicadorAvulsoDtoList.forEach(indicadorDto -> {

			IndicadorAvulso indicadorAvulso;

			Integer idIndicadorAvulso = indicadorDto.idIndicadorAvulso();

			if (idIndicadorAvulso != null) {

				indicadorAvulso = indicadorAvulsoRepository
						.findById(idIndicadorAvulso)
						.orElseThrow(() -> new RuntimeException("Indicador avulso não encontrado."));

			} else {

				indicadorAvulso = indicadorAvulsoRepository
						.save(new IndicadorAvulso(indicadorDto.indicadorAvulso()));

			}

			if (indicadorAvulso != null)
				indicadorAvulso = indicadorAvulsoRepository.save(indicadorAvulso);
			else
				throw new SiscapServiceException(
						Arrays.asList("Indicador avulso inválido."));

			// sincronizarMetasGlobaisIndicadorAvulso(
			// 		indicadorAvulso,
			// 		indicadorDto.indicadorAvulso() != null
			// 				? indicadorDto.indicadorAvulso().metasIndicadorAvulsoGeral()
			// 				: List.of());

			ProjetoIndicadorAvulso projetoIndicadorAvulso = buscarOuCriarProjetoIndicadorAvulso(projeto,
					indicadorAvulso, indicadorDto);

			sincronizarMetasProjetoIndicadorAvulso(
					projetoIndicadorAvulso,
					indicadorDto.metasProjeto());

			projetoIndicadorAvulsoSet.add(projetoIndicadorAvulso);

		});

		List<ProjetoIndicadorAvulso> projetoIndicadorAvulsoList = projetoIndicadorAvulsoRepository
				.saveAll(projetoIndicadorAvulsoSet);

		logger.info("Indicadores avulsos sincronizados com sucesso.");

		return new HashSet<>(projetoIndicadorAvulsoList);

	}

	private void removerIndicadoresNaoEnviados(Projeto projeto,
			List<ProjetoIndicadorAvulsoDto> projetoIndicadorAvulsoDtoList) {

		Set<Integer> idsRecebidos = projetoIndicadorAvulsoDtoList.stream()
				.map(ProjetoIndicadorAvulsoDto::id)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<ProjetoIndicadorAvulso> indicadoresExistentes = projetoIndicadorAvulsoRepository.findAllByProjeto(projeto);

		List<ProjetoIndicadorAvulso> indicadoresParaRemover = indicadoresExistentes.stream()
				.filter(indicador -> !idsRecebidos.contains(indicador.getId()))
				.toList();

		if (!indicadoresParaRemover.isEmpty()) {
			logger.info("Removendo relacao de indicadores avulsos com projeto não enviados: {}",
					indicadoresParaRemover.size());
			projetoIndicadorAvulsoRepository.deleteAll(indicadoresParaRemover);
		}

	}

	private ProjetoIndicadorAvulso buscarOuCriarProjetoIndicadorAvulso(
			Projeto projeto,
			IndicadorAvulso indicadorAvulso,
			ProjetoIndicadorAvulsoDto indicadorDto) {

		if (indicadorDto.id() != null) {
			return projetoIndicadorAvulsoRepository
					.findById(indicadorDto.id())
					.orElseThrow(() -> new RuntimeException("Indicador avulso do projeto não encontrado."));
		}

		ProjetoIndicadorAvulso novo = new ProjetoIndicadorAvulso();
		novo.setProjeto(projeto);
		novo.setIndicadorAvulso(indicadorAvulso);

		return projetoIndicadorAvulsoRepository.save(novo);

	}

	private void sincronizarMetasProjetoIndicadorAvulso(
			ProjetoIndicadorAvulso projetoIndicadorAvulso,
			List<ProjetoIndicadorAvulsoMetaDto> metasDto) {

		if (metasDto == null) {
			metasDto = List.of();
		}

		projetoIndicadorAvulsoMetaRepository
				.deleteByProjetoIndicadorAvulsoId(projetoIndicadorAvulso.getId());

		List<ProjetoIndicadorAvulsoMeta> metas = metasDto.stream()
				.map(metaDto -> new ProjetoIndicadorAvulsoMeta(projetoIndicadorAvulso, metaDto))
				.toList();

		if (metas.isEmpty())
			throw new SiscapServiceException(
					Arrays.asList("É obrigatória informar metas de indicadores de projetos estratégicos de um DIC."));

		projetoIndicadorAvulsoMetaRepository.saveAll(metas);

	}

	// private void sincronizarMetasGlobaisIndicadorAvulso(
	// 		IndicadorAvulso indicadorAvulso,
	// 		List<IndicadorAvulsoMetaDto> metasDto) {
	// 	// if (metasDto == null) {
	// 	// 	metasDto = List.of();
	// 	// }
	// 	// indicadorAvulsoMetaRepository.deleteByIndicadorAvulsoId(indicadorAvulso.getId());
	// 	// List<IndicadorAvulsoMeta> metas = metasDto.stream()
	// 	// 		.map(metaDto -> new IndicadorAvulsoMeta(indicadorAvulso, metaDto))
	// 	// 		.toList();
	// 	// if (metas.isEmpty())
	// 	// 	throw new SiscapServiceException(
	// 	// 			Arrays.asList("É obrigatória informar metas para um indicador projeto estratégico."));
	// 	// indicadorAvulsoMetaRepository.saveAll(metas);
	// 	return;
	// }

}