package br.gov.es.siscap.service;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.repository.PessoaOrganizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaOrganizacaoService {

	private final PessoaOrganizacaoRepository pessoaOrganizacaoRepository;


	// FUNCIONA!!!!!
	// tem que ver se injetar em OrganizacaoService causa erro
	// |-> Se sim: Lazy load?
	public Set<PessoaOrganizacao> buscarPorPessoa(Pessoa pessoa) {
//		return new HashSet<>(pessoaOrganizacaoRepository.findAllByPessoa(pessoa));
		return pessoaOrganizacaoRepository.findAllByPessoa(pessoa);
	}

	public Set<PessoaOrganizacao> buscarPorOrganizacao(Organizacao organizacao) {
		return pessoaOrganizacaoRepository.findAllByOrganizacao(organizacao);
	}

	public Set<PessoaOrganizacao> buscarPorIdPessoa(Long idPessoa) {
		return pessoaOrganizacaoRepository.findAllByIdPessoa(idPessoa);
	}

	public Set<PessoaOrganizacao> buscarPorIdOrganizacao(Long idOrganizacao) {
		return pessoaOrganizacaoRepository.findByAllByIdOrganizacao(idOrganizacao);
	}

	public Pessoa buscarResponsavelOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {

		return pessoaOrganizacaoSet.stream()
					.filter(PessoaOrganizacao::getResponsavel)
					.map(PessoaOrganizacao::getPessoa)
					.findFirst().orElse(null);
	}

	public void salvar() {

		// LOGICA DE SALVAR:
		// 1. Questão: Por Pessoa? Por Organização? Os dois?
		// 2. Verfificar se Set<PessoaOrganizacao> da(s) entidade(s) é nula; Se sim, new HashSet<>()
		// 3. Ver o retorno pra montar o DTO; Pessoa: idOrganizacao, isResponsavelOrganizacao / Organizacao: idPessoaResponsavel


	}

	public void atualizar() {

	}

	public void excluir() {

	}
}
