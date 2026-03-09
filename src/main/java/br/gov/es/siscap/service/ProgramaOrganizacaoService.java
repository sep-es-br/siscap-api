package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaOrganizacaoDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaOrganizacao;
import br.gov.es.siscap.models.ProgramaPessoa;
import br.gov.es.siscap.repository.ProgramaOrganizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramaOrganizacaoService {

	private final ProgramaOrganizacaoRepository repository;
	private final OrganizacaoService organizacaoService;
	private final Logger logger = LogManager.getLogger(ProgramaOrganizacaoService.class);

	public List<ProgramaOrganizacaoDto> buscarPorPrograma(Programa programa) {
		List<ProgramaOrganizacao> programaOrganizacaoList = this.buscarProgramaOrganizacaoSetPorPrograma(programa)
				.stream().toList();
		return this.montarListOrganizacoesDto(programaOrganizacaoList);
	}

	@Transactional
	public List<ProgramaOrganizacaoDto> cadastrar(Programa programa,
			List<ProgramaOrganizacaoDto> organizacoesPrograma) {

		logger.info("Cadastrando organizacões do Programa com id: {}", programa.getId());

		List<ProgramaOrganizacao> programaOrganizacaoList;

		Set<ProgramaOrganizacao> programaOrganizacaoSet = organizacoesPrograma.stream()
				.map(organizacaoProgamaDto -> {
					Organizacao organizacao = organizacaoService.buscar(organizacaoProgamaDto.id());
					return new ProgramaOrganizacao(programa, organizacao,
							organizacaoProgamaDto.papel());
				})
				.collect(Collectors.toSet());

		if (programaOrganizacaoSet == null) {
			throw new ValidacaoSiscapException(
					Arrays.asList("Não foi informado no programa as organizações envolvidas."));
		} else {
			programaOrganizacaoList = repository.saveAll(programaOrganizacaoSet);
		}

		logger.info("Equipe do programa cadastrada com sucesso");

		return this.montarListOrganizacoesDto(programaOrganizacaoList);

	}

	@Transactional
	public List<ProgramaOrganizacaoDto> atualizar(Programa programa,
			List<ProgramaOrganizacaoDto> orgaosEnvolvidosList) {

		logger.info("Alterando dados dos orgãos envolvidos do Programa com id: {}",
				programa.getId());

		Set<ProgramaOrganizacao> programaOrganizacaoSet = this.buscarProgramaOrganizacaoSetPorPrograma(programa);

		Set<ProgramaOrganizacao> organizacoesAlterarSet = new HashSet<>();
		Set<ProgramaOrganizacao> organizacoesAdicionarSet = new HashSet<>();

		orgaosEnvolvidosList.forEach( orgao -> 
			programaOrganizacaoSet
					.stream()
					.filter( orgaoPrograma -> orgaoPrograma.compararIdOrgaoComOrgaoProgramaDto(orgao) )
					.findFirst()
					.ifPresentOrElse(
							programaOrgao -> {
								programaOrgao.atualizarOrgaoPrograma(orgao);
								organizacoesAlterarSet.add(programaOrgao);
							},
							() -> { 
								Organizacao orgaoPrograma = organizacaoService.buscar(orgao.id());
								organizacoesAdicionarSet.add( new ProgramaOrganizacao( programa, orgaoPrograma, orgao.papel() ) );
							 } )
		);

		organizacoesAlterarSet.addAll(organizacoesAdicionarSet);

		repository.saveAllAndFlush(organizacoesAlterarSet);

		logger.info("Orgãos envolvidos do programa alterados com sucesso.");

		return this.buscarPorPrograma(programa);

	}

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

	private Set<ProgramaOrganizacao> buscarProgramaOrganizacaoSetPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	// private Set<ProgramaPessoa> buscarProgramaPessoaSetPorPessoa(Pessoa pessoa) {
	// return repository.findAllByPessoa(pessoa);
	// }

	private List<ProgramaOrganizacaoDto> montarListOrganizacoesDto(List<ProgramaOrganizacao> programaOrganizacaoList) {
		return programaOrganizacaoList.stream()
				.map(ProgramaOrganizacaoDto::new)
				.toList();
	}

}
