package br.gov.es.siscap.service;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.repository.PessoaOrganizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaOrganizacaoService {

	private final PessoaOrganizacaoRepository pessoaOrganizacaoRepository;
	private final Logger logger = LogManager.getLogger(PessoaOrganizacaoService.class);

	public Set<PessoaOrganizacao> buscarPorPessoa(Pessoa pessoa) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao da Pessoa com id: {}", pessoa.getId());
		return pessoaOrganizacaoRepository.findAllByPessoa(pessoa);
	}

	public Set<PessoaOrganizacao> buscarPorOrganizacao(Organizacao organizacao) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());
		return pessoaOrganizacaoRepository.findAllByOrganizacao(organizacao);
	}

	public Optional<Pessoa> buscarResponsavelOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		logger.info("Buscando responsavel pela Organizacao");

		return pessoaOrganizacaoSet.stream()
					.filter(PessoaOrganizacao::getResponsavel)
					.map(PessoaOrganizacao::getPessoa)
					.findFirst();
	}

	@Transactional
	public Set<PessoaOrganizacao> salvarPorPessoa(Pessoa pessoa, Set<Long> idOrganizacoes) {
		logger.info("Cadastrando vinculo entre Pessoa e Organizacao da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();

		idOrganizacoes.forEach((idOrganizacao) -> {
			PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(pessoa, new Organizacao(idOrganizacao));
			pessoaOrganizacaoSet.add(pessoaOrganizacao);
		});

		List<PessoaOrganizacao> pessoaOrganizacaoResultadoList = pessoaOrganizacaoRepository.saveAll(pessoaOrganizacaoSet);

		logger.info("Vinculo entre Pessoa e Organizacao por Pessoa cadastrado com sucesso");
		return new HashSet<>(pessoaOrganizacaoResultadoList);
	}

	@Transactional
	public Set<PessoaOrganizacao> salvarPorOrganizacao(Organizacao organizacao, Long idPessoaResponsavel) {
		logger.info("Cadastrando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();

		PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(new Pessoa(idPessoaResponsavel), organizacao);
		pessoaOrganizacao.setResponsavel(true);
		pessoaOrganizacaoSet.add(pessoaOrganizacao);

		List<PessoaOrganizacao> pessoaOrganizacaoResultadoList = pessoaOrganizacaoRepository.saveAll(pessoaOrganizacaoSet);

		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao cadastrado com sucesso");
		return new HashSet<>(pessoaOrganizacaoResultadoList);
	}

	@Transactional
	public Set<PessoaOrganizacao> atualizarPorPessoa(Pessoa pessoa, Set<Long> idOrganizacoes) {
		logger.info("Alterando vinculo entre Pessoa e Organizacao da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorPessoa(pessoa);

		if (this.compararPessoaOrganizacaoSetIdOrganizacoes(pessoaOrganizacaoSet, idOrganizacoes)) {
			return pessoaOrganizacaoSet;
		}

		Set<PessoaOrganizacao> pessoaOrganizacaoRemoverSet = this.removerPessoaOrganizacao(pessoaOrganizacaoSet, idOrganizacoes);

		Set<PessoaOrganizacao> pessoaOrganizacaoIncluirSet = this.incluirPessoaOrganizacao(pessoaOrganizacaoSet, idOrganizacoes, pessoa);

		pessoaOrganizacaoRemoverSet.addAll(pessoaOrganizacaoIncluirSet);

		pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoRemoverSet);

		logger.info("Vinculo entre Pessoa e Organizacao por Pessoa alterado com sucesso");
		return this.buscarPorPessoa(pessoa);
	}

	@Transactional
	public Set<PessoaOrganizacao> atualizarPorOrganizacao(Organizacao organizacao, Long idPessoaResponsavel) {
		logger.info("Alterando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorOrganizacao(organizacao);

		if (this.compararPessoaOrganizacaoSetIdPessoaResponsavel(pessoaOrganizacaoSet, idPessoaResponsavel)) {
			return pessoaOrganizacaoSet;
		}

		Set<PessoaOrganizacao> pessoaOrganizacaoDesvincularResponsavelSet = this.desvincularResponsavelPessoaOrganizacao(pessoaOrganizacaoSet);

		Set<PessoaOrganizacao> pessoaOrganizacaoVincularResponsavelSet = this.vincularResponsavelPessoaOrganizacao(organizacao, idPessoaResponsavel);

		pessoaOrganizacaoDesvincularResponsavelSet.addAll(pessoaOrganizacaoVincularResponsavelSet);

		pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoDesvincularResponsavelSet);

		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao alterado com sucesso");
		return this.buscarPorOrganizacao(organizacao);
	}

	@Transactional
	public void excluirPorPessoa(Pessoa pessoa) {
		logger.info("Excluindo vinculo entre Pessoa e Organizacao da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorPessoa(pessoa);

		pessoaOrganizacaoSet.forEach(PessoaOrganizacao::apagar);

		List<PessoaOrganizacao> pessoaOrganizacaoList = pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoSet);

		pessoaOrganizacaoRepository.deleteAll(pessoaOrganizacaoList);
		logger.info("Vinculo entre Pessoa e Organizacao por Pessoa excluido com sucesso");
	}

	@Transactional
	public void excluirPorOrganizacao(Organizacao organizacao) {
		logger.info("Excluindo vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorOrganizacao(organizacao);

		pessoaOrganizacaoSet.forEach(PessoaOrganizacao::apagar);

		List<PessoaOrganizacao> pessoaOrganizacaoList = pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoSet);

		pessoaOrganizacaoRepository.deleteAll(pessoaOrganizacaoList);
		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao excluido com sucesso");
	}

	private Set<Long> mapearPessoaOrganizacaoSetPorIdPessoa(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.map(PessoaOrganizacao::getPessoa)
					.map(Pessoa::getId)
					.collect(Collectors.toSet());
	}

	private Set<Long> mapearPessoaOrganizacaoSetPorIdOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.map(PessoaOrganizacao::getOrganizacao)
					.map(Organizacao::getId)
					.collect(Collectors.toSet());
	}

	private boolean compararPessoaOrganizacaoSetIdOrganizacoes(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoesSet) {
		return this.mapearPessoaOrganizacaoSetPorIdOrganizacao(pessoaOrganizacaoSet).equals(idOrganizacoesSet);
	}

	private boolean compararPessoaOrganizacaoSetIdPessoaResponsavel(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Long idPessoaResponsavel) {
		Pessoa responsavel = this.buscarResponsavelOrganizacao(pessoaOrganizacaoSet).orElse(null);

		if(responsavel != null) {
			return responsavel.getId().equals(idPessoaResponsavel);
		}

		return false;
	}

	private Set<PessoaOrganizacao> removerPessoaOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoes) {
		Set<PessoaOrganizacao> pessoaOrganizacaoRemoverSet = pessoaOrganizacaoSet
					.stream()
					.filter((pessoaOrganizacao) -> !idOrganizacoes.contains(pessoaOrganizacao.getOrganizacao().getId()))
					.collect(Collectors.toSet());

		pessoaOrganizacaoRemoverSet.forEach(PessoaOrganizacao::apagar);

		return pessoaOrganizacaoRemoverSet;
	}

	private Set<PessoaOrganizacao> incluirPessoaOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoes, Pessoa pessoa) {
		Set<PessoaOrganizacao> pessoaOrganizacaoIncluirSet = new HashSet<>();

		Set<Long> pessoaOrganizacaoIdOrganizacaoMapSet = this.mapearPessoaOrganizacaoSetPorIdOrganizacao(pessoaOrganizacaoSet);

		idOrganizacoes.forEach((idOrganizacao) -> {
			PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(pessoa, new Organizacao(idOrganizacao));
			if (!pessoaOrganizacaoIdOrganizacaoMapSet.contains(idOrganizacao)) {
				pessoaOrganizacaoIncluirSet.add(pessoaOrganizacao);
			}
		});

		return pessoaOrganizacaoIncluirSet;
	}

	private Set<PessoaOrganizacao> desvincularResponsavelPessoaOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {

		Set<PessoaOrganizacao> pessoaOrganizacaoDesvincularResponsavelSet = pessoaOrganizacaoSet
					.stream()
					.filter(PessoaOrganizacao::getResponsavel)
					.collect(Collectors.toSet());

		pessoaOrganizacaoDesvincularResponsavelSet.forEach(PessoaOrganizacao::apagar);

		return pessoaOrganizacaoDesvincularResponsavelSet;

	}

	private Set<PessoaOrganizacao> vincularResponsavelPessoaOrganizacao(Organizacao organizacao, Long idPessoaResponsavel) {

		Set<PessoaOrganizacao> pessoaOrganizacaoVincularResponsavelSet = new HashSet<>();

		PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(new Pessoa(idPessoaResponsavel), organizacao);
		pessoaOrganizacao.setResponsavel(true);
		pessoaOrganizacaoVincularResponsavelSet.add(pessoaOrganizacao);

		return pessoaOrganizacaoVincularResponsavelSet;
	}
}
