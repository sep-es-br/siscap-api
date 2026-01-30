package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProgramaAssinaturaEdocsDto;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;
import br.gov.es.siscap.repository.ProgramaAssinaturaEdocsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaAssinaturaEdocsService {

	private final ProgramaAssinaturaEdocsRepository repository;
	private final PessoaService pessoaService;
	private final Logger logger = LogManager.getLogger(ProgramaPessoaService.class);

	public List<ProgramaAssinaturaEdocsDto> buscarPorPrograma(Programa programa) {
		List<ProgramaAssinaturaEdocs> programaAssinaturaEdocsList = this.buscarProgramaAssinaturasSetPorPrograma(programa).stream().toList();
		return this.montarListaDto(programaAssinaturaEdocsList);
	}

	@Transactional
	public List<ProgramaAssinaturaEdocsDto> cadastrar(Programa programa, List<String> assinantes) {

		logger.info("Cadastrando assinaturas do Programa com id: {}", programa.getId());

		Set<ProgramaAssinaturaEdocs> programaPessoasAssinantesSet = assinantes.stream()
				.map(assinante -> {
					Pessoa pessoa = pessoaService.buscarPorSub(assinante);
					return new ProgramaAssinaturaEdocs(programa, pessoa);
				})
				.collect(Collectors.toSet());

		List<ProgramaAssinaturaEdocs> programaPessoaAssinanteEdocsList = repository.saveAll(programaPessoasAssinantesSet);

		logger.info("Assinantes do programa cadastrados com sucesso");

		return this.montarListaDto(programaPessoaAssinanteEdocsList);

	}

	private List<ProgramaAssinaturaEdocsDto> montarListaDto(List<ProgramaAssinaturaEdocs> programaPessoaAssinanteEdocsList) {
		return programaPessoaAssinanteEdocsList.stream()
				.map(ProgramaAssinaturaEdocsDto::new)
				.toList();
				
	}

	// @Transactional
	// public List<EquipeDto> atualizar(Programa programa, List<EquipeDto>
	// equipeCaptacao) {
	// logger.info("Alterando dados da equipe do Programa com id: {}",
	// programa.getId());

	// Set<ProgramaPessoa> programaPessoaSet =
	// this.buscarProgramaPessoaSetPorPrograma(programa);

	// Set<ProgramaPessoa> membrosEquipeAlterarSet = new HashSet<>();

	// Set<ProgramaPessoa> membrosEquipeAdicionarSet = new HashSet<>();

	// equipeCaptacao.forEach(equipeDto -> {
	// programaPessoaSet
	// .stream()
	// .filter(programaPessoa ->
	// programaPessoa.compararIdPessoaComEquipeDto(equipeDto))
	// .findFirst()
	// .ifPresentOrElse(
	// (programaPessoa) -> {
	// programaPessoa.atualizarMembroEquipe(equipeDto);
	// membrosEquipeAlterarSet.add(programaPessoa);
	// },
	// () -> {
	// membrosEquipeAdicionarSet.add(new ProgramaPessoa(programa, equipeDto));
	// }
	// );
	// });

	// membrosEquipeAlterarSet.addAll(membrosEquipeAdicionarSet);

	// repository.saveAllAndFlush(membrosEquipeAlterarSet);

	// logger.info("Equipe do programa alterada com sucesso");

	// return this.buscarPorPrograma(programa);
	// }

	// @Transactional
	// public void excluirPorPrograma(Programa programa) {
	// logger.info("Excluindo equipe do Programa com id: {}", programa.getId());

	// Set<ProgramaPessoa> programaPessoaSet =
	// this.buscarProgramaPessoaSetPorPrograma(programa);

	// programaPessoaSet.forEach(programaPessoa -> programaPessoa.apagar("Programa
	// excluido"));

	// List<ProgramaPessoa> programaPessoaList =
	// repository.saveAllAndFlush(programaPessoaSet);

	// repository.deleteAll(programaPessoaList);

	// logger.info("Equipe do programa excluida com sucesso");
	// }

	// @Transactional
	// public void excluirPorPessoa(Pessoa pessoa) {
	// logger.info("Excluindo Pessoa com id: {} da(s) equipe(s) de programa",
	// pessoa.getId());

	// Set<ProgramaPessoa> programaPessoaSet =
	// this.buscarProgramaPessoaSetPorPessoa(pessoa);

	// programaPessoaSet.forEach(programaPessoa -> programaPessoa.apagar("Pessoa
	// excluida"));

	// List<ProgramaPessoa> programaPessoaList =
	// repository.saveAllAndFlush(programaPessoaSet);

	// repository.deleteAll(programaPessoaList);

	// logger.info("Pessoa excluida da(s) equipe(s) de programa com sucesso");
	// }

	private Set<ProgramaAssinaturaEdocs> buscarProgramaAssinaturasSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private Set<ProgramaAssinaturaEdocs> buscarProgramaPessoaSetPorPessoa(Pessoa pessoa) {
		return repository.findAllByPessoa(pessoa);
	}

	
}
