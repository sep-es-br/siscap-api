package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaPessoa;
import br.gov.es.siscap.repository.ProgramaPessoaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaPessoaService {

	private final ProgramaPessoaRepository repository;
	private final Logger logger = LogManager.getLogger(ProgramaPessoaService.class);

	public List<EquipeDto> buscarPorPrograma(Programa programa) {
		List<ProgramaPessoa> programaPessoaList = this.buscarProgramaPessoaSetPorPrograma(programa).stream().toList();

		return this.montarListEquipeDto(programaPessoaList);
	}

	@Transactional
	public List<EquipeDto> salvar(Programa programa, List<EquipeDto> equipeCaptacao) {
		logger.info("Cadastrando equipe do Programa com id: {}", programa.getId());

		Set<ProgramaPessoa> programaPessoaSet = equipeCaptacao.stream()
					.map(equipeDto -> new ProgramaPessoa(programa, equipeDto))
					.collect(Collectors.toSet());

		List<ProgramaPessoa> programaPessoaList = repository.saveAll(programaPessoaSet);

		logger.info("Equipe do programa cadastrada com sucesso");
		return this.montarListEquipeDto(programaPessoaList);
	}

	@Transactional
	public List<EquipeDto> atualizar(Programa programa, List<EquipeDto> equipeCaptacao) {
		logger.info("Alterando dados da equipe do Programa com id: {}", programa.getId());

		Set<ProgramaPessoa> programaPessoaSet = this.buscarProgramaPessoaSetPorPrograma(programa);

		Set<ProgramaPessoa> membrosEquipeAlterarSet = new HashSet<>();

		Set<ProgramaPessoa> membrosEquipeAdicionarSet = new HashSet<>();

		equipeCaptacao.forEach(equipeDto -> {
			programaPessoaSet
						.stream()
						.filter(programaPessoa -> programaPessoa.compararIdPessoaComEquipeDto(equipeDto))
						.findFirst()
						.ifPresentOrElse(
									(programaPessoa) -> {
										programaPessoa.atualizarMembroEquipe(equipeDto);
										membrosEquipeAlterarSet.add(programaPessoa);
									},
									() -> {
										membrosEquipeAdicionarSet.add(new ProgramaPessoa(programa, equipeDto));
									}
						);
		});

		membrosEquipeAlterarSet.addAll(membrosEquipeAdicionarSet);

		List<ProgramaPessoa> programaPessoaList = repository.saveAllAndFlush(membrosEquipeAlterarSet);

		logger.info("Equipe do programa alterada com sucesso");
		return this.buscarPorPrograma(programa);
	}

	@Transactional
	public void excluirPorPrograma(Programa programa) {
		logger.info("Excluindo equipe do Programa com id: {}", programa.getId());

		Set<ProgramaPessoa> programaPessoaSet = this.buscarProgramaPessoaSetPorPrograma(programa);

		programaPessoaSet.forEach(programaPessoa -> programaPessoa.apagar("Programa excluido"));

		List<ProgramaPessoa> programaPessoaList = repository.saveAllAndFlush(programaPessoaSet);

		repository.deleteAll(programaPessoaList);

		logger.info("Equipe do programa excluida com sucesso");
	}

	@Transactional
	public void excluirPorPessoa(Pessoa pessoa) {
		logger.info("Excluindo Pessoa com id: {} da(s) equipe(s) de programa", pessoa.getId());

		Set<ProgramaPessoa> programaPessoaSet = this.buscarProgramaPessoaSetPorPessoa(pessoa);

		programaPessoaSet.forEach(programaPessoa -> programaPessoa.apagar("Pessoa excluida"));

		List<ProgramaPessoa> programaPessoaList = repository.saveAllAndFlush(programaPessoaSet);

		repository.deleteAll(programaPessoaList);

		logger.info("Pessoa excluida da(s) equipe(s) de programa com sucesso");
	}

	private Set<ProgramaPessoa> buscarProgramaPessoaSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private Set<ProgramaPessoa> buscarProgramaPessoaSetPorPessoa(Pessoa pessoa) {
		return repository.findAllByPessoa(pessoa);
	}

	private List<EquipeDto> montarListEquipeDto(List<ProgramaPessoa> programaPessoaList) {
		return programaPessoaList.stream()
					.map(EquipeDto::new)
					.toList();
	}
}
