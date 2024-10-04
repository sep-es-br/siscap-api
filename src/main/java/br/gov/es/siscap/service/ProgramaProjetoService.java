package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoPropostoDto;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaProjeto;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProgramaProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramaProjetoService {

	private final ProgramaProjetoRepository repository;
	private final Logger logger = LogManager.getLogger(ProgramaProjetoService.class);

	public List<ProjetoPropostoDto> buscarPorPrograma(Programa programa) {
		List<ProgramaProjeto> programaProjetoList = this.buscarProgramaProjetoSetPorPrograma(programa).stream().toList();

		return this.montarListProjetoPropostoDto(programaProjetoList);
	}

	@Transactional
	public List<ProjetoPropostoDto> cadastrar(Programa programa, List<ProjetoPropostoDto> projetosPropostos) {
		logger.info("Cadastrando projetos propostos para o Programa com id: {}", programa.getId());

		Set<ProgramaProjeto> programaProjetoSet = projetosPropostos.stream()
					.map(projetoPropostoDto -> new ProgramaProjeto(programa, projetoPropostoDto))
					.collect(Collectors.toSet());

		List<ProgramaProjeto> programaProjetoList = repository.saveAll(programaProjetoSet);

		logger.info("Projetos propostos cadastrados com sucesso");
		return this.montarListProjetoPropostoDto(programaProjetoList);
	}

	@Transactional
	public List<ProjetoPropostoDto> atualizar(Programa programa, List<ProjetoPropostoDto> projetosPropostos) {
		logger.info("Atualizando dados de projetos propostos para o Programa com id: {}", programa.getId());

		Set<ProgramaProjeto> programaProjetoSet = this.buscarProgramaProjetoSetPorPrograma(programa);

		Set<ProgramaProjeto> programaProjetoRemoverSet = new HashSet<>();

		Set<ProgramaProjeto> programaProjetoAlterarSet = new HashSet<>();

		Set<ProgramaProjeto> programaProjetoAdicionarSet = new HashSet<>();

		programaProjetoSet.forEach(
					programaProjeto -> {
						if (projetosPropostos
									.stream()
									.noneMatch(programaProjeto::compararIdProjetoComProjetoPropostoDto)) {
							programaProjeto.apagarProjetoProposto();
							programaProjetoRemoverSet.add(programaProjeto);
						}
					});

		projetosPropostos
					.forEach(projetoPropostoDto -> {
						programaProjetoSet
									.stream()
									.filter(programaProjeto -> programaProjeto.compararIdProjetoComProjetoPropostoDto(projetoPropostoDto))
									.findFirst()
									.ifPresentOrElse(
												(programaProjeto) -> {
													if (!Objects.equals(programaProjeto.getValor(), projetoPropostoDto.valor())) {
														programaProjeto.apagarProjetoProposto();
														programaProjetoAlterarSet.add(programaProjeto);
														programaProjetoAlterarSet.add(new ProgramaProjeto(programa, projetoPropostoDto));
													}
												},
												() -> programaProjetoAdicionarSet.add(new ProgramaProjeto(programa, projetoPropostoDto))
									);
					});

		programaProjetoRemoverSet.addAll(programaProjetoAlterarSet);

		programaProjetoRemoverSet.addAll(programaProjetoAdicionarSet);

		List<ProgramaProjeto> programaProjetoList = repository.saveAllAndFlush(programaProjetoRemoverSet);

		logger.info("Projetos propostos atualizados com sucesso");
		return this.buscarPorPrograma(programa);
	}

	@Transactional
	public void excluirPorPrograma(Programa programa) {
		logger.info("Excluindo projetos propostos do Programa com id: {}", programa.getId());

		Set<ProgramaProjeto> programaProjetoSet = this.buscarProgramaProjetoSetPorPrograma(programa);

		programaProjetoSet.forEach(ProgramaProjeto::apagarProjetoProposto);

		List<ProgramaProjeto> programaProjetoList = repository.saveAllAndFlush(programaProjetoSet);

		repository.deleteAll(programaProjetoList);
		logger.info("Projetos propostos excluidos com sucesso");
	}

	@Transactional
	public void excluirPorProjeto(Projeto projeto) {
		logger.info("Excluindo projeto proposto com id: {} do(s) Programa(s)", projeto.getId());

		Set<ProgramaProjeto> programaProjetoSet = this.buscarProgramaProjetoSetPorProjeto(projeto);

		programaProjetoSet.forEach(ProgramaProjeto::apagarProjetoProposto);

		List<ProgramaProjeto> programaProjetoList = repository.saveAllAndFlush(programaProjetoSet);

		repository.deleteAll(programaProjetoList);
		logger.info("Projeto proposto excluido do(s) Programa(s) com sucesso");
	}

	private Set<ProgramaProjeto> buscarProgramaProjetoSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private Set<ProgramaProjeto> buscarProgramaProjetoSetPorProjeto(Projeto projeto) {
		return repository.findAllByProjeto(projeto);
	}

	private List<ProjetoPropostoDto> montarListProjetoPropostoDto(List<ProgramaProjeto> programaProjetoList) {
		return programaProjetoList.stream()
					.map(ProjetoPropostoDto::new)
					.toList();
	}
}
