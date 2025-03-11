package br.gov.es.siscap.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.enums.TipoOrganizacaoEnum;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.TipoOrganizacao;
import br.gov.es.siscap.repository.OrganizacaoRepository;
import br.gov.es.siscap.repository.PessoaOrganizacaoRepository;
import br.gov.es.siscap.repository.PessoaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaOrganizacaoService {

	private final PessoaOrganizacaoRepository pessoaOrganizacaoRepository;
	private final OrganizacaoRepository organizacaoRepository;
	private final PessoaRepository pessoaRepository;
	private final AcessoCidadaoService acessoCidadaoService;
	private final Logger logger = LogManager.getLogger(PessoaOrganizacaoService.class);

	@PostConstruct
	public void init() {
		this.sincronizarResponsaveisOrganizacoesBancoComAcessoCidadaoAPI();
	}

	public Set<PessoaOrganizacao> buscarPorPessoa(Pessoa pessoa) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao(oes) da Pessoa com id: {}", pessoa.getId());
		return pessoaOrganizacaoRepository.findAllByPessoa(pessoa);
	}

	public PessoaOrganizacao buscarPorOrganizacao(Organizacao organizacao) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao da Organizacao com id: {}", organizacao.getId());
		return pessoaOrganizacaoRepository.findByOrganizacaoAndIsResponsavelTrue(organizacao);
	}

	public List<PessoaOrganizacao> buscarPorIds(List<Long> ids) {
		logger.info("Buscando vinculo entre Pessoa e Organizacao com id: {}", ids.stream().map(Object::toString).collect(Collectors.joining(", ")));
		return pessoaOrganizacaoRepository.findAllById(ids);
	}

	@Transactional
	public Set<PessoaOrganizacao> salvarPessoaOrganizacaoSetAutenticacaoUsuario(Set<PessoaOrganizacao> pessoaOrganizacaoSet) {

		List<PessoaOrganizacao> pessoaOrganizacaoList = pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoSet);

		return new HashSet<>(pessoaOrganizacaoList);
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

		if (pessoaOrganizacao == null) {
			logger.info("MEDIDA PROVISORIA ATE TODAS AS ORGANIZACOES POSSUIREM UM RESPONSAVEL");
			return this.cadastrarPorOrganizacao(organizacao, idPessoaResponsavel);
		}

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

	@Transactional
	public void excluirTodosPorId(List<Long> organizacoesId) {
		// logger.info("Excluindo os vinculos entre Pessoa e Organizacao com id: {}", String.join(", ", organizacoesId));

		List<PessoaOrganizacao> pessoasOrganizacao = this.buscarPorIds(organizacoesId);

		pessoasOrganizacao.forEach(PessoaOrganizacao::apagarPessoaOrganizacao);

		pessoaOrganizacaoRepository.saveAllAndFlush(pessoasOrganizacao);
		pessoaOrganizacaoRepository.deleteAllById(pessoasOrganizacao.stream().map(PessoaOrganizacao::getId).toList());
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

	@Transactional
	protected void sincronizarResponsaveisOrganizacoesBancoComAcessoCidadaoAPI() {
		logger.info("Sincronizando dados de Pessoa e PessoaOrganizacao (Responsavel) do banco da aplicacao com os dados da API Acesso Cidadao");

		Set<Organizacao> organizacaoSet = organizacaoRepository.findAllByGuidNotNullAndTipoOrganizacao(new TipoOrganizacao(TipoOrganizacaoEnum.SECRETARIA.getValue()));

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();

		organizacaoSet.forEach((organizacao) -> {

			try {
				ACAgentePublicoPapelDto gestorConjunto = acessoCidadaoService.buscarGestorNovoConjuntoPorGuidOrganizacao(organizacao.getGuid());
				Optional<Pessoa> pessoaResponsavelOpt = pessoaRepository.buscarPorSubOuNomeTratado(gestorConjunto.AgentePublicoSub(), gestorConjunto.AgentePublicoNome());

				pessoaResponsavelOpt.ifPresentOrElse(
							(pessoa) -> {
								Pessoa pessoaAtualizada = this.atualizarDadosPessoaBancoPorAcessoCidadaoAPI(pessoa, gestorConjunto);
								pessoaOrganizacaoSet.addAll(this.validarResponsavelOrganizacaoComPessoa(pessoaAtualizada, organizacao));
							},
							() -> {
								Pessoa novaPessoa = this.cadastrarNovaPessoaBancoPorAcessoCidadaoAPI(gestorConjunto);
								pessoaOrganizacaoSet.addAll(this.validarResponsavelOrganizacaoComPessoa(novaPessoa, organizacao));
							}
				);
			} catch (Exception e) {
				logger.error("Erro ao buscar papel do gestor do conjunto [guid: {}, nome: {}]", organizacao.getGuid(), organizacao.getNome());
			}
		});

		if (!(pessoaOrganizacaoSet.isEmpty())) {
			pessoaOrganizacaoRepository.saveAllAndFlush(pessoaOrganizacaoSet);
		}

		logger.info("Dados de Pessoa e PessoaOrganizacao (Responsavel) sincronizados com sucesso");
	}

	@Transactional
	protected Pessoa atualizarDadosPessoaBancoPorAcessoCidadaoAPI(Pessoa pessoa, ACAgentePublicoPapelDto gestorConjunto) {
		logger.info("Atualizando dados de Pessoa do banco de dados da aplicacao com os dados da API Acesso Cidadao (contexto papel do gestor do conjunto)");

		if (pessoa.getSub() == null)
			pessoa.setSub(gestorConjunto.AgentePublicoSub());

		return pessoaRepository.saveAndFlush(pessoa);
	}

	@Transactional
	protected Pessoa cadastrarNovaPessoaBancoPorAcessoCidadaoAPI(ACAgentePublicoPapelDto gestorConjunto) {
		logger.info("Cadastrando nova Pessoa do banco de dados da aplicacao com os dados da API Acesso Cidadao (contexto papel do gestor do conjunto)");

		Pessoa tempPessoa = new Pessoa();
		tempPessoa.setNome(gestorConjunto.AgentePublicoNome());
		tempPessoa.setSub(gestorConjunto.AgentePublicoSub());

		return pessoaRepository.saveAndFlush(tempPessoa);
	}

	private Set<PessoaOrganizacao> validarResponsavelOrganizacaoComPessoa(Pessoa pessoa, Organizacao organizacao) {

		Set<PessoaOrganizacao> pessoaOrganizacaoSet = new HashSet<>();

		PessoaOrganizacao responsavelBanco = this.buscarPorOrganizacao(organizacao);

		PessoaOrganizacao responsavelAPI = new PessoaOrganizacao(pessoa, organizacao);
		responsavelAPI.setIsResponsavel(true);

		if (responsavelBanco == null) {

			pessoaOrganizacaoSet.add(responsavelAPI);
			return pessoaOrganizacaoSet;
		}

		if (!(responsavelBanco.getPessoa().getId().equals(responsavelAPI.getPessoa().getId()))) {

			responsavelBanco.setIsResponsavel(false);
			pessoaOrganizacaoSet.add(responsavelBanco);
			pessoaOrganizacaoSet.add(responsavelAPI);
		}

		return pessoaOrganizacaoSet;
	}
}
