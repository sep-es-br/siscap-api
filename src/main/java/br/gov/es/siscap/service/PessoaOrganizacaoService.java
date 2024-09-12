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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaOrganizacaoService {

	private final PessoaOrganizacaoRepository pessoaOrganizacaoRepository;
	private final Logger logger = LogManager.getLogger(PessoaOrganizacaoService.class);

	public Set<PessoaOrganizacao> buscarPorPessoa(Pessoa pessoa) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao(oes) da Pessoa com id: {}", pessoa.getId());
		return pessoaOrganizacaoRepository.findAllByPessoa(pessoa);
	}

	public PessoaOrganizacao buscarPorOrganizacao(Organizacao organizacao) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());
		return pessoaOrganizacaoRepository.findByOrganizacaoAndIsResponsavelTrue(organizacao);
	}

	@Transactional
	public Set<PessoaOrganizacao> cadastrarPorPessoa(Pessoa pessoa, Set<Long> idOrganizacoes) {
		logger.info("Cadastrando vinculo entre Pessoa e Organizacao(oes) da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();

		idOrganizacoes.forEach((idOrganizacao) -> {
			PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(pessoa, new Organizacao(idOrganizacao));
			pessoaOrganizacaoSet.add(pessoaOrganizacao);
		});

		List<PessoaOrganizacao> pessoaOrganizacaoResultadoList = pessoaOrganizacaoRepository.saveAll(pessoaOrganizacaoSet);

		logger.info("Vinculo entre Pessoa e Organizacao(oes) por Pessoa cadastrado com sucesso");
		return new HashSet<>(pessoaOrganizacaoResultadoList);
	}

	@Transactional
	public PessoaOrganizacao cadastrarPorOrganizacao(Organizacao organizacao, Long idPessoaResponsavel) {
		logger.info("Cadastrando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		PessoaOrganizacao pessoaOrganizacao = new PessoaOrganizacao(new Pessoa(idPessoaResponsavel), organizacao);
		pessoaOrganizacao.setIsResponsavel(true);

		PessoaOrganizacao pessoaOrganizacaoResultado = pessoaOrganizacaoRepository.save(pessoaOrganizacao);

		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao cadastrado com sucesso");
		return pessoaOrganizacaoResultado;
	}


	@Transactional
	public Set<PessoaOrganizacao> atualizarPorPessoa(Pessoa pessoa, Set<Long> idOrganizacoes) {
		logger.info("Atualizando vinculo entre Pessoa e Organizacao(oes) da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorPessoa(pessoa);

		if (this.compararPessoaOrganizacaoSetComIdOrganizacoesSet(pessoaOrganizacaoSet, idOrganizacoes)) {
			return pessoaOrganizacaoSet;
		}

		Set<PessoaOrganizacao> pessoaOrganizacaoRemoverSet = this.removerPessoaDaOrganizacao(pessoaOrganizacaoSet, idOrganizacoes);

		Set<PessoaOrganizacao> pessoaOrganizacaoIncluirSet = this.incluirPessoaNaOrganizacao(pessoaOrganizacaoSet, idOrganizacoes, pessoa);

		pessoaOrganizacaoRemoverSet.addAll(pessoaOrganizacaoIncluirSet);

		pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoRemoverSet);

		logger.info("Vinculo entre Pessoa e Organizacao(oes) por Pessoa atualizado com sucesso");
		return this.buscarPorPessoa(pessoa);
	}

	@Transactional
	public PessoaOrganizacao atualizarPorOrganizacao(Organizacao organizacao, Long idPessoaResponsavel) {
		logger.info("Atualizando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		PessoaOrganizacao pessoaOrganizacao = this.buscarPorOrganizacao(organizacao);

		if (pessoaOrganizacao.getPessoa().getId().equals(idPessoaResponsavel)) {
			return pessoaOrganizacao;
		}

		pessoaOrganizacao.apagarPessoaOrganizacao();

		PessoaOrganizacao pessoaOrganizacaoNovoResponsavel = new PessoaOrganizacao(new Pessoa(idPessoaResponsavel), organizacao);
		pessoaOrganizacaoNovoResponsavel.setIsResponsavel(true);

		pessoaOrganizacaoRepository.saveAndFlush(pessoaOrganizacao);
		PessoaOrganizacao pessoaOrganizacaoResultado = pessoaOrganizacaoRepository.save(pessoaOrganizacaoNovoResponsavel);

		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao atualizado com sucesso");
		return this.buscarPorOrganizacao(organizacao);
	}

	@Transactional
	public void excluirPorPessoa(Pessoa pessoa) {
		logger.info("Excluindo vinculo entre Pessoa e Organizacao da Pessoa com id: {}", pessoa.getId());

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = this.buscarPorPessoa(pessoa);

		pessoaOrganizacaoSet.forEach(PessoaOrganizacao::apagarPessoaOrganizacao);

		List<PessoaOrganizacao> pessoaOrganizacaoList = pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoSet);
		pessoaOrganizacaoRepository.deleteAll(pessoaOrganizacaoList);
		logger.info("Vinculo entre Pessoa e Organizacao por Pessoa excluido com sucesso");
	}

	@Transactional
	public void excluirPorOrganizacao(Organizacao organizacao) {
		logger.info("Excluindo vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());

		PessoaOrganizacao pessoaOrganizacao = this.buscarPorOrganizacao(organizacao);

		pessoaOrganizacao.apagarPessoaOrganizacao();

		pessoaOrganizacaoRepository.saveAndFlush(pessoaOrganizacao);
		pessoaOrganizacaoRepository.delete(pessoaOrganizacao);
		logger.info("Vinculo entre Pessoa e Organizacao por Organizacao excluido com sucesso");
	}

	private Set<Long> mapearPessoaOrganizacaoSetPorIdOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		return pessoaOrganizacaoSet
					.stream()
					.map(PessoaOrganizacao::getOrganizacao)
					.map(Organizacao::getId)
					.collect(Collectors.toSet());
	}

	private boolean compararPessoaOrganizacaoSetComIdOrganizacoesSet(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoesSet) {
		return this.mapearPessoaOrganizacaoSetPorIdOrganizacao(pessoaOrganizacaoSet).equals(idOrganizacoesSet);
	}

	private Set<PessoaOrganizacao> removerPessoaDaOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoes) {
		Set<PessoaOrganizacao> pessoaOrganizacaoRemoverSet = pessoaOrganizacaoSet
					.stream()
					.filter((pessoaOrganizacao) -> !idOrganizacoes.contains(pessoaOrganizacao.getOrganizacao().getId()))
					.collect(Collectors.toSet());

		pessoaOrganizacaoRemoverSet.forEach(PessoaOrganizacao::apagarPessoaOrganizacao);

		return pessoaOrganizacaoRemoverSet;
	}

	private Set<PessoaOrganizacao> incluirPessoaNaOrganizacao(Set<PessoaOrganizacao> pessoaOrganizacaoSet, Set<Long> idOrganizacoes, Pessoa pessoa) {
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
}
