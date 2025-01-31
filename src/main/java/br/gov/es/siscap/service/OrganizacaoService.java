package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.OrganizacaoNaoEncontradaException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.TipoOrganizacao;
import br.gov.es.siscap.repository.OrganizacaoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizacaoService {

	private final OrganizacaoRepository repository;
	private final OrganogramaService organogramaService;
	private final ImagemPerfilService imagemPerfilService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;
	private final CidadeService cidadeService;
	private final EstadoService estadoService;
	private final PaisService paisService;
	private final TipoOrganizacaoService tipoOrganizacaoService;
	private final Logger logger = LogManager.getLogger(OrganizacaoService.class);

	@PostConstruct
	protected void init() {

		/*
			31/01/2025
			TODO: SINCRONIZAR RESPONSAVEL DA ORGANIZACAO NO BANCO DE DADOS COM OS DADOS VINDOS DA API ACESSOCIDADAO

			- UTILIZAR GUID DA ORGANIZACAO PARA ALIMENTAR CHAMADA DA API ACESSO CIDADAO (ENDPOINT: /api/conjunto/{guid}/gestornovo/papel)
			- UTILIZAR SUB DO AGENTE PUBLICO (PROPRIEDADE: AgentePublicoSub) PARA BUSCAR DADOS DO USUARIO NA API ACESSO CIDADAO (ACUserInfoDto)
			- UTILIZAR SUB OU NOME DA PESSOA PARA BUSCAR ENTIDADE "PESSOA" NO BANCO
			- VINCULAR PESSOA COM ORGANIZACAO COMO RESPONSAVEL (PessoaOrganizacao)

			* PROBLEMAS:
			  |-> ALGUMAS ENTIDADES "PESSOA" NO BANCO NAO POSSUEM SUB
			      |-> FALHA AO BUSCAR "PESSOA" POR SUB
			  |-> ALGUMAS ENTIDADES "PESSOA" NO BANCO ESTAO COM O NOME ~ERRADO~
			      |-> ACUserInfoDto ATUALMENTE TRAZ PROPRIEDADE 'apelido' AO INVES DO NOME COMPLETO [REQUER LIBERACAO DA PRODEST PARA ACESSAR O SCOPE 'nome' NA AUTENTICACAO DE USUARIO]
			        |-> FALHA AO BUSCAR "PESSOA" POR NOME
		*/

		this.sincronizarOrganizacoesBancoComOrganogramaAPI();
	}

	public Page<OrganizacaoListaDto> listarTodos(Pageable pageable, String search) {
		logger.info("Buscando todas as organizacoes");

		return repository.paginarOrganizacoesPorFiltroPesquisaSimples(search, pageable)
					.map(organizacao -> {
						try {
							return new OrganizacaoListaDto(organizacao, this.getImagemNotNull(organizacao.getNomeImagem()));
						} catch (IOException e) {
							throw new SiscapServiceException(Collections.singletonList(e.getMessage()));
						}
					});
	}

	public List<OpcoesDto> listarOpcoesDropdown(Long filtroTipoOrganizacao) {

		Sort organizacaoListSort = Sort.by(Sort.Direction.ASC, "nome");

		List<Organizacao> organizacaoList = filtroTipoOrganizacao != null
					? repository.findAllByTipoOrganizacao(new TipoOrganizacao(filtroTipoOrganizacao), organizacaoListSort)
					: repository.findAll(organizacaoListSort);

		return organizacaoList.stream().map(OpcoesDto::new).toList();
	}

	public OrganizacaoDto buscarPorId(Long id) throws IOException {
		logger.info("Buscando organizacao com id: {}", id);

		Organizacao organizacao = this.buscar(id);

		/*
			12/09/2024
			MEDIDA PROVISORIA ATE TODAS AS ORGANIZACOES POSSUIREM UM RESPONSAVEL
		*/
		PessoaOrganizacao pessoaOrganizacao = pessoaOrganizacaoService.buscarPorOrganizacao(organizacao);
		Long idPessoaResponsavel = pessoaOrganizacao != null ? pessoaOrganizacao.getPessoa().getId() : null;

		return new OrganizacaoDto(organizacao, this.getImagemNotNull(organizacao.getNomeImagem()), idPessoaResponsavel);
	}

	@Transactional
	public OrganizacaoDto cadastrar(OrganizacaoForm form) throws IOException {
		logger.info("Cadastrando nova organizacao");
		logger.info("Dados: {}", form);

		this.validarOrganizacao(form, true);

		String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());

		Organizacao organizacao = repository.save(new Organizacao(form, nomeImagem));
		Long idPessoaResponsavel = pessoaOrganizacaoService.cadastrarPorOrganizacao(organizacao, form.idPessoaResponsavel()).getPessoa().getId();

		logger.info("Organizacao cadastrada com sucesso");
		return new OrganizacaoDto(organizacao, this.getImagemNotNull(organizacao.getNomeImagem()), idPessoaResponsavel);
	}

	@Transactional
	public OrganizacaoDto atualizar(Long id, OrganizacaoForm form) throws IOException {
		logger.info("Atualizando organizacao com id: {}", id);
		logger.info("Dados: {}", form);

		this.validarOrganizacao(form, false);

		Organizacao organizacao = this.buscar(id);
		organizacao.atualizarOrganizacao(form);

		if (form.imagemPerfil() != null)
			organizacao.atualizarImagemPerfil(imagemPerfilService.atualizar(organizacao.getNomeImagem(), form.imagemPerfil()));

		Organizacao organizacaoResultado = repository.save(organizacao);
		Long idPessoaResponsavel = pessoaOrganizacaoService.atualizarPorOrganizacao(organizacaoResultado, form.idPessoaResponsavel()).getPessoa().getId();

		logger.info("Organizacao atualizada com sucesso");
		return new OrganizacaoDto(organizacaoResultado, this.getImagemNotNull(organizacaoResultado.getNomeImagem()), idPessoaResponsavel);
	}

	@Transactional
	public void excluir(Long id) {
		logger.info("Excluindo organizacao com id: {}", id);

		Organizacao organizacao = this.buscar(id);

		imagemPerfilService.apagar(organizacao.getNomeImagem());
		organizacao.apagarOrganizacao();
		repository.saveAndFlush(organizacao);
		repository.deleteById(organizacao.getId());

		pessoaOrganizacaoService.excluirPorOrganizacao(organizacao);

		logger.info("Organizacao excluida com sucesso");
	}

	public boolean existePorId(Long id) {
		return repository.existsById(id);
	}

	public Optional<Organizacao> buscarPorCnpj(String cnpj) {
		return repository.findByCnpj(cnpj);
	}

	private Organizacao buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new OrganizacaoNaoEncontradaException(id));
	}

	private byte[] getImagemNotNull(String nomeImagem) throws IOException {
		if (nomeImagem == null)
			return null;

		Resource imagemPerfil = imagemPerfilService.buscar(nomeImagem);
		byte[] conteudoImagem = null;

		if (imagemPerfil != null)
			conteudoImagem = imagemPerfil.getContentAsByteArray();

		return conteudoImagem;
	}

	private void validarOrganizacao(OrganizacaoForm form, boolean isSalvar) {
		List<String> erros = new ArrayList<>();

		boolean checkFormIdCidadeNotNullExistePorId = form.idCidade() != null && !cidadeService.existePorId(form.idCidade());
		boolean checkFormIdEstadoNotNullExistePorId = form.idEstado() != null && !estadoService.existePorId(form.idEstado());
		boolean checkFormIdPaisExistePorId = !paisService.existePorId(form.idPais());
		boolean checkFormIdOrganizacaoPaiNotNullExistePorId = form.idOrganizacaoPai() != null && !repository.existsById(form.idOrganizacaoPai());
		boolean checkFormIdTipoOrganizacaoExistePorId = !tipoOrganizacaoService.existePorId(form.idTipoOrganizacao());
		boolean checkFormCnpjNotNullSePaisBrasil = form.idPais().equals(1L) && form.cnpj() == null;
		boolean checkFormCnpjNotNullExistePorCnpj = form.cnpj() != null && repository.existsByCnpj(form.cnpj());

		if (checkFormIdCidadeNotNullExistePorId)
			erros.add("Erro ao encontrar cidade com id " + form.idCidade());

		if (checkFormIdEstadoNotNullExistePorId)
			erros.add("Erro ao encontrar estado com id " + form.idEstado());

		if (checkFormIdPaisExistePorId)
			erros.add("Erro ao encontrar país com id " + form.idPais());

		if (checkFormIdOrganizacaoPaiNotNullExistePorId)
			erros.add("Erro ao encontrar organização pai com id " + form.idOrganizacaoPai());

		if (checkFormIdTipoOrganizacaoExistePorId)
			erros.add("Erro ao encontrar tipo de organização com id " + form.idTipoOrganizacao());

		if (checkFormCnpjNotNullSePaisBrasil)
			erros.add("CNPJ é obrigatório para organizações do Brasil.");

		if (checkFormCnpjNotNullExistePorCnpj && isSalvar)
			erros.add("Já existe uma organização cadastrada com esse CNPJ.");


		if (!erros.isEmpty()) {
			erros.forEach(logger::warn);
			throw new ValidacaoSiscapException(erros);
		}
	}

	@Transactional
	protected void sincronizarOrganizacoesBancoComOrganogramaAPI() {
		logger.info("Sincronizando dados de Organizacao do banco de dados da aplicacao com os dados da API Organograma");

		List<Organizacao> organizacaoOrganogramaAPIList = organogramaService.listarOrganizacoesFilhasGOVES()
					.stream()
					.map(Organizacao::new)
					.toList();

		List<Organizacao> organizacaoBancoList = new ArrayList<>();

		organizacaoOrganogramaAPIList.forEach(
					orgAPI -> {
						Optional<Organizacao> orgBancoOpt = repository.findByCnpjOrNomeFantasia(orgAPI.getCnpj(), orgAPI.getNomeFantasia());

						if (orgBancoOpt.isPresent()) {
							Organizacao orgBanco = this.atualizarDadosOrganizacaoBancoPorOrganogramaOrganizacaoDTO(orgBancoOpt.get(), orgAPI);
							organizacaoBancoList.add(orgBanco);
						} else {
							organizacaoBancoList.add(orgAPI);
						}
					}
		);

		repository.saveAllAndFlush(organizacaoBancoList);
		logger.info("Dados de Organizacao sincronizados com sucesso");
	}

	private Organizacao atualizarDadosOrganizacaoBancoPorOrganogramaOrganizacaoDTO(Organizacao orgBanco, Organizacao orgAPI) {

		if (orgBanco.getGuid() == null) {
			orgBanco.setGuid(orgAPI.getGuid());
		}

		if (!(orgBanco.getCnpj().equals(orgAPI.getCnpj()))) {
			orgBanco.setCnpj(orgAPI.getCnpj());
		}

		if (!(orgBanco.getTipoOrganizacao().getId().equals(orgAPI.getTipoOrganizacao().getId()))) {
			orgBanco.setTipoOrganizacao(orgAPI.getTipoOrganizacao());
		}

		if (orgBanco.getPais() == null || !(orgBanco.getPais().getId().equals(orgAPI.getPais().getId()))) {
			orgBanco.setPais(orgAPI.getPais());
		}

		if (orgBanco.getEstado() == null || !(orgBanco.getEstado().getId().equals(orgAPI.getEstado().getId()))) {
			orgBanco.setEstado(orgAPI.getEstado());
		}

		return orgBanco;
	}
}