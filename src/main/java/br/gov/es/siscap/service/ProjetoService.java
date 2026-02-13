package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.*;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ProjetoPropostoOpcoesDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.enums.LotacaoUsuarioEnum;
import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.enums.TipoPapelEnum;
import br.gov.es.siscap.exception.RelatorioNomeArquivoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.models.TipoMotivoArquivamento;
import br.gov.es.siscap.repository.ProjetoRepository;
import br.gov.es.siscap.specification.ProjetoSpecification;
import br.gov.es.siscap.utils.FormatadorCountAno;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoCamposComplementacao;
import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.ProjetoParecer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

	private final ProjetoRepository repository;
	private final ProjetoPessoaService projetoPessoaService;
	private final LocalidadeQuantiaService localidadeQuantiaService;
	private final OrganizacaoService organizacaoService;
	private final PessoaService pessoaService;
	private final ProjetoIndicadorService projetoIndicadorService;
	private final PessoaOrganizacaoService pessoaOrganizacaoService;
	private final EmailService emailService;
	private final ProjetoAcaoService projetoAcaoService;
	private final TipoMotivoArquivamentoService tipoMotivoArquivamentoService;
	private final AcessoCidadaoService acessoCidadaoService;
	private final AutenticacaoService autenticacaoService;
	private final RegrasDePermissaoService regrasDePermissaoService;
	private final ProjetoComplementosService projetoComplementosService;
	private final ProjetoParecerService projetoParecerService;
	private final UsuarioService usuarioService;

	@PersistenceContext
	private EntityManager entityManager;

	private final Logger logger = LogManager.getLogger(ProjetoService.class);

	@Value("${frontend.host}")
	private String frontEndHost;

	@Value("${api.parecer.guidSUBEPP}")
	private String guidSUBEPP;

	@Value("${api.parecer.guidSUBEO}")
	private String guidSUBEO;

	@Value("${api.edocs.guiddestinoSUBCAP}")
	private String guidSUBCAP;

	@Value("${email.gerencia-subcap}")
	private String DESTINO_GERENCIA_SUBCAP;

	@Value("${email.destinatario-subcap}")
	private String DESTINO_SUBCAP;

	public Page<ProjetoListaDto> listarTodos(
			Pageable pageable,
			String siglaOuTitulo,
			Long idOrganizacao,
			String status) {

		Specification<Projeto> especificacaoSiglaTitulo = siglaOuTitulo.isBlank() ? null
				: ProjetoSpecification.filtroSiglaTitulo(siglaOuTitulo);
		Specification<Projeto> especificacaoIdOrganizacao = idOrganizacao == 0 ? null
				: ProjetoSpecification.filtroIdOrganizacao(idOrganizacao);
		Specification<Projeto> especificacaoStatus = status.equals("Status") ? null
				: ProjetoSpecification.filtroStatus(status);

		String subUsuario = autenticacaoService.getUsuarioLogado();

		LotacaoUsuarioEnum lotacaoUsuario = LotacaoUsuarioEnum.fromGuid(
				usuarioService.lotacaoGuidUsuario(subUsuario),
				guidSUBEPP,
				guidSUBEO,
				guidSUBCAP);

		boolean podeVerParecerSEP = lotacaoUsuario == LotacaoUsuarioEnum.SUBEPP
				|| lotacaoUsuario == LotacaoUsuarioEnum.SUBEO;

		Specification<Projeto> filtroBase = Specification
				.where(especificacaoSiglaTitulo)
				.and(especificacaoStatus);

		Specification<Projeto> filtroOrganizacao = especificacaoIdOrganizacao;

		Specification<Projeto> filtroParecerSEP = ProjetoSpecification.filtroStatusParecerSEP();

		Specification<Projeto> filtroPesquisa;

		if (podeVerParecerSEP) {
			filtroPesquisa = filtroBase.and(
					Specification.where(filtroOrganizacao)
							.or(filtroParecerSEP));
		} else {
			filtroPesquisa = filtroBase.and(filtroOrganizacao);
		}

		return repository.findAll(filtroPesquisa, pageable)
				.map(projeto -> {
					Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

					ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

					return new ProjetoListaDto(projeto, valorDto.quantia(), lotacaoUsuario.getValue());
				});

	}

	public List<ProjetoPropostoOpcoesDto> listarOpcoesDropdown() {
		return repository.findAll(Sort.by(Sort.Direction.ASC, "titulo"))
				.stream()
				.filter(Projeto::isAtivo)
				.map(projeto -> {
					Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

					ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

					boolean parecerGEOCEnviado = projetoParecerService.verificarEnvioParecereGEOCProjeto(projeto.getId());

					return new ProjetoPropostoOpcoesDto(projeto, valorDto, parecerGEOCEnviado);
				})
				.toList();
	}

	public ProjetoDto buscarPorId(Long id) {

		logger.info("Buscando projeto com id: {}", id);

		Projeto projeto = Optional.ofNullable(this.buscar(id))
				.orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado para o ID: " + id));

		Set<ProjetoPessoa> projetoPessoaSet = projetoPessoaService.buscarPorProjeto(projeto);

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.buscarPorProjeto(projeto);

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		Set<ProjetoIndicador> indicadores = projetoIndicadorService.buscarPorProjeto(projeto);

		Set<ProjetoAcao> acoes = projetoAcaoService.buscarPorProjeto(projeto);

		String subUsuario = autenticacaoService.getUsuarioLogado();

		Boolean podeEditarEmAnalise = regrasDePermissaoService.podeEditar(subUsuario, projeto);

		Boolean podeSolicitarComplementacao = regrasDePermissaoService.podeSolicitarComplementacao(subUsuario, projeto);

		Boolean podeResponderComplementacao = regrasDePermissaoService.podeReenviarDICEmComplementacao(
				this.subEhResponsavelProponenteProjeto(subUsuario, projeto.getId()), projeto);

		Set<ProjetoCamposComplementacao> complementosSeremFeitos = projetoComplementosService.buscarPorProjeto(projeto);

		ProjetoParecer parecerProjeto = projetoParecerService.buscarPorProjeto(projeto).stream()
				.filter(parecer -> parecer.getGuidUnidadeOrganizacao()
						.equals(usuarioService.lotacaoGuidUsuario(subUsuario)))
				.findFirst()
				.orElse(null);

		LotacaoUsuarioEnum lotacaoUsuario = LotacaoUsuarioEnum.fromGuid(
				usuarioService.lotacaoGuidUsuario(subUsuario),
				guidSUBEPP,
				guidSUBEO,
				guidSUBCAP);

		ProjetoDto projetoDtoRetorno = new ProjetoDto(projeto, valorDto, rateio,
				this.buscarIdResponsavelProponente(projetoPessoaSet),
				this.buscarEquipeElaboracao(projetoPessoaSet),
				this.buscarSubResponsavelProponente(projetoPessoaSet),
				this.buscarIndicadores(indicadores),
				this.buscarAcoes(acoes),
				this.buscarSubProponente(projetoPessoaSet),
				this.buscarLotacaoResponsavelProponente(projetoPessoaSet),
				this.buscarNomeResponsavelProponente(projetoPessoaSet),
				podeEditarEmAnalise,
				podeSolicitarComplementacao,
				podeResponderComplementacao,
				projeto.getIdProcessoEdocs(),
				projeto.getIdDocumentoCapturadoEdocs(),
				this.buscarComplementacoes(complementosSeremFeitos),
				this.buscarParecer(parecerProjeto),
				lotacaoUsuario.getValue(),
				projeto.getProjetoParecerSet().stream().map(ProjetoParecerDto::new).toList(),
				this.buscarNomeProponente(projetoPessoaSet));

		return projetoDtoRetorno;

	}

	private List<ProjetoIndicadorDto> buscarIndicadores(Set<ProjetoIndicador> projetoIndicadorSet) {
		return projetoIndicadorSet.stream()
				.map(ProjetoIndicadorDto::new)
				.toList();
	}

	private List<ProjetoAcaoDto> buscarAcoes(Set<ProjetoAcao> projetoAcaoSet) {
		return projetoAcaoSet.stream()
				.map(ProjetoAcaoDto::new)
				.toList();
	}

	private List<ProjetoCamposComplementacaoDto> buscarComplementacoes(
			Set<ProjetoCamposComplementacao> projetoCamposComplementacaoSet) {
		return projetoCamposComplementacaoSet
				.stream()
				.map(campo -> {
					return new ProjetoCamposComplementacaoDto(campo, null);
				})
				.toList();
	}

	private ProjetoParecerDto buscarParecer(ProjetoParecer projetoParecer) {

		if (projetoParecer == null)
			return null;

		String nomeUsuarioEnviou = "";
		if (projetoParecer.getSubUsuarioEnviou() != null) {
			Pessoa pessoa = pessoaService.buscarPorSub(projetoParecer.getSubUsuarioEnviou());
			nomeUsuarioEnviou = pessoa.getNome();
		}

		return new ProjetoParecerDto(projetoParecer, nomeUsuarioEnviou);

	}

	@Transactional
	public ProjetoDto cadastrar(ProjetoForm form, boolean rascunho) {

		logger.info("Cadastrando novo projeto");

		this.validarProjeto(form, true);

		Projeto tempProjeto = new Projeto(form);

		tempProjeto.setCountAno(this.buscarCountAnoFormatado());
		tempProjeto.setRascunho(true);
		tempProjeto.setStatus(StatusProjetoEnum.EM_ELABORACAO.getValue());

		Projeto projeto = repository.save(tempProjeto);

		Set<ProjetoPessoa> projetoPessoaSet;

		List<EquipeDto> equipeParaGravar = form.equipeElaboracao();

		List<EquipeDto> equipeElaboracaoValidada = this.validarEquipeElaboracao(form);
		if (!new HashSet<>(form.equipeElaboracao()).equals(new HashSet<>(equipeElaboracaoValidada))) {
			equipeParaGravar = equipeElaboracaoValidada;
		}

		projetoPessoaSet = projetoPessoaService.cadastrar(projeto, form.idResponsavelProponente(), equipeParaGravar);

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.cadastrar(projeto, form.valor(),
				form.rateio());

		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		List<ProjetoIndicadorDto> indicadoresProjetoParaGravar = form.indicadoresProjeto();

		projetoIndicadorService.cadastrar(projeto, indicadoresProjetoParaGravar);

		List<ProjetoAcaoDto> acoesProjetoParaGravar = form.acoesProjeto();

		projetoAcaoService.cadastrar(projeto, acoesProjetoParaGravar);

		try {
			if (form.enviarProjetoGestor()) {

				logger.info("Envio email para gestor");

				String subResponsavelProponente = this.buscarSubResponsavelProponente(projetoPessoaSet);

				PessoaDto pessoaProponenteDto = pessoaService.buscarPorId(this.buscarIdProponente(projetoPessoaSet));

				String nomeProponente = pessoaProponenteDto.nome();

				this.enviarEmailGestorAvaliarDic(projeto.getId(), subResponsavelProponente, nomeProponente);

			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		// atualiza com o que ficou no gravado no banco apos commit
		entityManager.refresh(projeto);

		logger.info("Projeto cadastrado com sucesso");

		return new ProjetoDto(projeto, valorDto, rateio,
				this.buscarIdResponsavelProponente(projetoPessoaSet),
				this.buscarEquipeElaboracao(projetoPessoaSet),
				this.buscarSubResponsavelProponente(projetoPessoaSet),
				indicadoresProjetoParaGravar,
				acoesProjetoParaGravar,
				this.buscarSubProponente(projetoPessoaSet),
				this.buscarLotacaoResponsavelProponente(projetoPessoaSet),
				this.buscarNomeResponsavelProponente(projetoPessoaSet),
				false,
				false,
				false, null, null, null, null, null,
				projeto.getProjetoParecerSet().stream().map(ProjetoParecerDto::new).toList(),
				this.buscarNomeProponente(projetoPessoaSet));

	}

	@Transactional
	public ProjetoDto atualizar(Long id, ProjetoForm form, boolean rascunho) {

		logger.info("Atualizando projeto com id: {}", id);

		this.validarProjeto(form, false);

		Projeto projeto = this.buscar(id);

		projeto.atualizarProjeto(form);

		projeto.setRascunho(true);

		Projeto projetoResult = repository.save(projeto);

		Set<ProjetoPessoa> projetoPessoaSet;

		List<EquipeDto> equipeParaGravar = form.equipeElaboracao();

		List<EquipeDto> equipeElaboracaoValidada = this.validarEquipeElaboracao(form);

		if (!new HashSet<>(form.equipeElaboracao()).equals(new HashSet<>(equipeElaboracaoValidada))) {
			equipeParaGravar = equipeElaboracaoValidada;
		}

		projetoPessoaSet = projetoPessoaService.atualizar(projeto, form.idResponsavelProponente(), equipeParaGravar);

		// forçar a atualizacao do SUB do responsavel novo
		projetoPessoaSet.stream()
				.filter(p -> p.getPessoa().getId().equals(form.idResponsavelProponente())
						&& p.getTipoPapel().getId().equals(TipoPapelEnum.RESPONSAVEL_PROPONENTE.getValue()))
				.findFirst()
				.ifPresent(p -> {
					String subResponsavelProponente = pessoaService.buscarSubPorId(p.getPessoa().getId());
					p.getPessoa().setSub(subResponsavelProponente);
				});

		List<ProjetoIndicadorDto> projetoIndicadoresDto = form.indicadoresProjeto();
		Set<ProjetoIndicador> projetoIndicadoresSet = projetoIndicadorService.atualizar(projetoResult,
				projetoIndicadoresDto);

		Set<LocalidadeQuantia> localidadeQuantiaSet = localidadeQuantiaService.atualizar(projetoResult, form.valor(),
				form.rateio());
		ValorDto valorDto = localidadeQuantiaService.montarValorDto(localidadeQuantiaSet);

		List<RateioDto> rateio = localidadeQuantiaService.montarListRateioDtoPorProjeto(localidadeQuantiaSet);

		List<ProjetoAcaoDto> projetoAcoesDto = form.acoesProjeto();
		Set<ProjetoAcao> projetoAcoesSet = projetoAcaoService.atualizar(projetoResult, projetoAcoesDto, rascunho);

		String subResponsavelProponente = this.buscarSubResponsavelProponente(projetoPessoaSet);

		String nomeProponente = this.buscarNomeProponente(projetoPessoaSet);

		ProjetoParecerDto projetoParecerDto = form.parecerProjetoUsuario();
		ProjetoParecer projetoParecer = null;

		if (projeto.getStatus().equals(StatusProjetoEnum.PARECER_SEP.getValue())
				|| projeto.getStatus().equals(StatusProjetoEnum.ELEGIBILIDADE.getValue())) {

			projetoParecerDto = form.parecerProjetoUsuario();

			if (projetoParecerDto.id() == null)
				projetoParecer = projetoParecerService.cadastrar(projetoResult, projetoParecerDto);
			else
				projetoParecer = projetoParecerService.atualizar(projetoResult, projetoParecerDto);

		}

		try {

			if (form.enviarProjetoGestor()) {
				logger.info("Envio email para gestor");
				this.enviarEmailGestorAvaliarDic(id, subResponsavelProponente, nomeProponente);
			}

			if (form.enviarProjetoPedirParecer()) {
				logger.info("Envio email para solicitar pareceres Estrategico e Orçamentario");
				if (this.enviarEmailPareceresEstrategicoOrcamentario(id, subResponsavelProponente, nomeProponente)) {
					this.alterarStatusProjeto(id, StatusProjetoEnum.PARECER_SEP.getValue());
					entityManager.flush();
				}
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

		// atualiza com o que ficou no gravado no banco apos commit
		entityManager.refresh(projeto);

		logger.info("Projeto atualizado com sucesso");

		return new ProjetoDto(projetoResult, valorDto, rateio,
				this.buscarIdResponsavelProponente(projetoPessoaSet),
				this.buscarEquipeElaboracao(projetoPessoaSet),
				subResponsavelProponente,
				this.buscarIndicadores(projetoIndicadoresSet),
				this.buscarAcoes(projetoAcoesSet),
				this.buscarSubProponente(projetoPessoaSet),
				this.buscarLotacaoResponsavelProponente(projetoPessoaSet),
				this.buscarNomeResponsavelProponente(projetoPessoaSet),
				false,
				false,
				false,
				null, null,
				null,
				this.buscarParecer(projetoParecer), null,
				projeto.getProjetoParecerSet().stream().map(ProjetoParecerDto::new).toList(),
				this.buscarNomeProponente(projetoPessoaSet));

	}

	@Transactional
	public boolean excluir(Long id, String justificativa) {

		logger.info("Excluindo projeto com id: {}", id);

		Projeto projeto = this.buscar(id);

		// o DIC estando em alguns status especificos vamos fazer a exclusao logica
		// porem
		// registrando uma justificativa obrigatoria..
		if (List.of(StatusProjetoEnum.EM_ANALISE.getValue(), StatusProjetoEnum.COMPLEMETACAO.getValue(),
				StatusProjetoEnum.PARECER_SEP.getValue())
				.contains(projeto.getStatus())) {

			if (justificativa == null || justificativa.isEmpty())
				throw new ValidacaoSiscapException(List.of("Justificativa para exclusão do DIC não informada."));

			projeto.setJustificativaExclusaoLogica(justificativa);

			this.alterarStatusProjeto(id, StatusProjetoEnum.ENCERRADO.getValue());

			this.exclusaoLogica(projeto);

		} else if (projeto.getStatus().equals(StatusProjetoEnum.EM_ELABORACAO.getValue())) {

			// faz exclusao fisica do projeto e seus dependentes..
			this.exclusaoFisica(projeto);

		} else {

			logger.info("Fazer exclusao lógica pois status do DIC esta fora do tratado.", id);

			this.exclusaoLogica(projeto);

		}

		logger.info("Projeto excluido com sucesso");

		return true;

	}

	@Transactional
	private void exclusaoLogica(Projeto projeto) {

		projeto.apagarProjeto();

		projetoPessoaService.excluirPorProjeto(projeto);

		localidadeQuantiaService.excluir(projeto);

		projetoIndicadorService.excluirPorProjeto(projeto);

		projetoAcaoService.excluirPorProjeto(projeto);

		projetoParecerService.excluirPorProjeto(projeto);

		repository.deleteById(projeto.getId());

	}

	@Transactional
	private void exclusaoFisica(Projeto projeto) {

		projetoPessoaService.excluirFisicamentePorProjeto(projeto);

		localidadeQuantiaService.excluirFisicamentePorProjeto(projeto);

		projetoIndicadorService.excluirFisicamentePorProjeto(projeto);

		projetoAcaoService.excluirFisicamentePorProjeto(projeto);

		projetoParecerService.excluirFisicamentePorProjeto(projeto);

		repository.saveAndFlush(projeto);

		repository.deleteFisico(projeto.getId());

	}

	@Transactional
	public void registrarMotivoArquivamentoProjeto(Long id, String codigoMotivoArquivamento, String justificativa) {

		List<String> erros = new ArrayList<>();
		Projeto projeto = this.buscar(id);

		TipoMotivoArquivamento motivoArquivamento = tipoMotivoArquivamentoService
				.buscarTipoMotivoCodigo(codigoMotivoArquivamento);

		if (motivoArquivamento != null) {
			projeto.setTipoMotivoArquivamento(motivoArquivamento);
			projeto.setJustificativaArquivamento(justificativa);
		} else {
			erros.add("Erro ao enviar aviso de arquivamento do projeto id " + id
					+ " motivo arquivamento não foi informado.");
			throw new ValidacaoSiscapException(erros);
		}

		repository.save(projeto);

	}

	@Transactional
	public void alterarStatusProjeto(Long id, String novoStatus) {

		logger.info("Alterando status do projeto {} para {}.", id, novoStatus);

		StatusProjetoEnum novoStatusEnum = StatusProjetoEnum.fromDescricao(novoStatus);

		Projeto projeto = this.buscar(id);

		projeto.setStatus(novoStatus);

		novoStatusEnum.validar(projeto);

		repository.save(projeto);

	}

	@Transactional
	public void inserirComplementacoesSeremRealizadasDIC(Projeto projeto,
			List<ProjetoCamposComplementacaoDto> complementos) {

		if (projeto.getProjetoComplementoSet().size() > 0) {
			logger.info(
					"Projeto id {} ja possui complementos definidos e serão excluidos logicamente para inserção do novo pedido.",
					projeto.getId());
			projetoComplementosService.excluirPorProjeto(projeto);
		}

		projetoComplementosService.cadastrar(projeto, complementos);

		repository.save(projeto);

	}

	@Transactional
	public void atualizarProtocoloProcessoEdocsProjeto(Long id, String protocoloEdcos) {
		Projeto projeto = this.buscar(id);

		projeto.setProtocoloEdocs(protocoloEdcos);
		projeto.setRascunho(false);
		projeto.setStatus(StatusProjetoEnum.EM_ANALISE.getValue());

		repository.save(projeto);
	}

	@Transactional
	public void enviarSolicitacaoRevisaoProjeto(Long id, String justificativa) {

		List<String> erros = new ArrayList<>();

		if (justificativa == null || justificativa.isEmpty() || justificativa.isBlank()) {
			erros.add("Erro ao enviar solicitação de revisão do projeto id " + id
					+ " justificativa não presente no pedido de envio.");
			throw new ValidacaoSiscapException(erros);
		}

		Projeto projeto = this.buscar(id);

		Optional<Pessoa> proponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		Optional<Pessoa> responsavelProponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isResponsavelProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		if (proponenteProjeto.isPresent() && responsavelProponenteProjeto.isPresent()) {

			List<String> emailsInteressadosList = new ArrayList<String>();

			emailsInteressadosList.add(proponenteProjeto.get().getEmail());

			boolean confirmacaoEnvioEmail;
			try {

				confirmacaoEnvioEmail = emailService.enviarEmailRevisarProjeto(emailsInteressadosList,
						justificativa,
						proponenteProjeto.get().getNome(),
						projeto, responsavelProponenteProjeto.get().getNome());

				if (confirmacaoEnvioEmail) {

					logger.info("Email enviado com sucesso");

					this.alterarStatusProjeto(id, StatusProjetoEnum.EM_ELABORACAO.getValue());

				} else {
					erros.add("Erro ao enviar solicitação de revisão do projeto id " + id);
				}

			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			}

		} else {
			erros.add("Não foi possível fazer o envio pois o proponente não foi encontrado - projeto id " + id);
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return;

	}

	@Transactional
	public boolean enviarAvisoSolicitarComplementacaoProjeto(Long id,
			List<ProjetoCamposComplementacaoDto> complementos) {

		List<String> erros = new ArrayList<>();

		if (complementos.isEmpty()) {
			erros.add("Erro ao enviar solicitação para complementação do projeto id " + id
					+ " motivos para complementação não informadas.");
			throw new ValidacaoSiscapException(erros);
		}

		Projeto projeto = this.buscar(id);

		Optional<Pessoa> proponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		Optional<Pessoa> responsavelProponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isResponsavelProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		if (proponenteProjeto.isPresent() && responsavelProponenteProjeto.isPresent()) {

			boolean confirmacaoEnvioEmail;
			List<String> emailsInteressadosList = new ArrayList<String>();
			emailsInteressadosList.add(proponenteProjeto.get().getEmail());

			try {

				confirmacaoEnvioEmail = emailService.enviarEmailComplemetacaoProjeto(emailsInteressadosList,
						proponenteProjeto.get().getNome(),
						responsavelProponenteProjeto.get().getNome(),
						projeto.getSigla(),
						complementos, projeto.getId());

				if (confirmacaoEnvioEmail) {
					logger.info(
							"Email aviso solicitação de complementação do projeto enviado com sucesso para o projeto id "
									+ id);
					this.alterarStatusProjeto(id, StatusProjetoEnum.COMPLEMETACAO.getValue());
					this.inserirComplementacoesSeremRealizadasDIC(projeto, complementos);
				} else {
					erros.add("Erro ao enviar aviso para complementação do projeto id " + id);
				}

			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			}

		} else {
			erros.add("Não foi possível fazer o envio pois o proponente não foi encontrado - projeto id " + id);
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return true;

	}

	public void enviarEmailGerenciaSubcap(Long idDIC) {

		List<String> erros = new ArrayList<>();
		boolean confirmacaoEnvioEmail;
		List<String> emailsInteressadosList = new ArrayList<String>();
		emailsInteressadosList.add(DESTINO_GERENCIA_SUBCAP);

		Projeto projeto = Optional.ofNullable(this.buscar(idDIC))
				.orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado para o ID: " + idDIC));

		try {

			confirmacaoEnvioEmail = emailService.enviarEmailAvisoParecerGerenciaSubcap(emailsInteressadosList,
					projeto.getTitulo(), idDIC);

			if (confirmacaoEnvioEmail) {
				logger.info("Email aviso  aviso de parecer gerencia SUBCAP enviado com sucesso.");
			} else {
				erros.add("Erro ao enviar aviso de parecer gerencia SUBCAP ");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return;

	}

	public void enviarEmailGerenciaSubcapDicAutuado(Long idDIC) {

		List<String> erros = new ArrayList<>();
		boolean confirmacaoEnvioEmail;
		List<String> emailsInteressadosList = new ArrayList<String>();
		emailsInteressadosList.add(DESTINO_SUBCAP);

		Projeto projeto = Optional.ofNullable(this.buscar(idDIC))
				.orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado para o ID: " + idDIC));

		try {

			confirmacaoEnvioEmail = emailService.enviarEmailAvisoSubcapDicAutuado(emailsInteressadosList,
					projeto.getTitulo(), idDIC);

			if (confirmacaoEnvioEmail) {
				logger.info("Email aviso DIC autuado enviado com sucesso.");
			} else {
				erros.add("Erro ao enviar aviso DIC autuado para SUBCAP ");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return;

	}

	public void enviarEmailSubSecretariaSubcap(Long idDIC) {

		List<String> erros = new ArrayList<>();
		boolean confirmacaoEnvioEmail;
		List<String> emailsInteressadosList = new ArrayList<String>();
		emailsInteressadosList.add(DESTINO_GERENCIA_SUBCAP);

		Projeto projeto = Optional.ofNullable(this.buscar(idDIC))
				.orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado para o ID: " + idDIC));

		try {

			confirmacaoEnvioEmail = emailService.enviarEmailAvisoParecerGeocSubcapRealizado(emailsInteressadosList,
					projeto.getTitulo(), idDIC);

			if (confirmacaoEnvioEmail) {
				logger.info("Email aviso de parecer gerencia SUBCAP entranhado enviado com sucesso.");
			} else {
				erros.add("Erro ao enviar aviso de parecer gerencia SUBCAP ");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			erros.add(e.getMessage());
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return;

	}

	@Transactional
	public void enviarAvisoArquivamentoProjeto(Long id, String justificativa, String codigoMotivoArquivamento) {

		List<String> erros = new ArrayList<>();

		if (codigoMotivoArquivamento == null) {
			erros.add("Erro ao enviar solicitação de revisão do projeto id " + id
					+ " código do motivo de arquivamento não foi informado.");
			throw new ValidacaoSiscapException(erros);
		}

		if ((justificativa == null || justificativa.isEmpty() || justificativa.isBlank())
				&& (codigoMotivoArquivamento != null && codigoMotivoArquivamento.trim().equals("M011"))) {
			erros.add("Erro ao enviar solicitação de revisão do projeto id " + id
					+ " justificativa não presente no pedido de envio para motivo OUTROS.");
			throw new ValidacaoSiscapException(erros);
		}

		if (justificativa == null)
			justificativa = "";

		Projeto projeto = this.buscar(id);

		Optional<Pessoa> proponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		Optional<Pessoa> responsavelProponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(pessoa -> pessoa.isResponsavelProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		if (proponenteProjeto.isPresent() && responsavelProponenteProjeto.isPresent()) {

			List<String> emailsInteressadosList = new ArrayList<String>();
			boolean confirmacaoEnvioEmail;

			emailsInteressadosList.add(proponenteProjeto.get().getEmail());

			TipoMotivoArquivamento tipoMotivoArquivamento = tipoMotivoArquivamentoService
					.buscarTipoMotivoCodigo(codigoMotivoArquivamento);

			try {

				confirmacaoEnvioEmail = emailService.enviarEmailArquivamentorProjeto(emailsInteressadosList,
						justificativa,
						proponenteProjeto.get().getNome(),
						projeto.getSigla(),
						codigoMotivoArquivamento,
						tipoMotivoArquivamento.getTipo(),
						responsavelProponenteProjeto.get().getNome(),
						projeto.getId());

				if (confirmacaoEnvioEmail) {
					logger.info("Email aviso arquivamento projeto enviado com sucesso do projeto id " + id);
					this.alterarStatusProjeto(id, StatusProjetoEnum.ARQUIVADO.getValue());
					this.registrarMotivoArquivamentoProjeto(id, codigoMotivoArquivamento, justificativa);
				} else {
					erros.add("Erro ao enviar aviso de arquivamento do projeto id " + id);
				}

			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			}

		} else {
			erros.add("Não foi possível fazer o envio pois o proponente não foi encontrado - projeto id " + id);
		}

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

		return;

	}

	public List<Long> buscarIdProjetoPropostoList(Programa programa) {
		logger.info("Buscando projetos vinculados ao programa com id: {}", programa.getId());

		return this.buscarProjetosPorPrograma(programa)
				.stream()
				.map(Projeto::getId)
				.toList();
	}

	public List<OpcoesDto> buscarProjetosPropostos(Programa programa) {
		return this.buscarProjetosPorPrograma(programa)
				.stream()
				.map(OpcoesDto::new)
				.toList();
	}

	@Transactional
	public List<Long> vincularProjetosAoPrograma(Programa programa, List<Long> idProjetoPropostoList) {
		logger.info("Vinculando projetos ao programa com id: {}", programa.getId());
		logger.info("Ids dos projetos: {}", idProjetoPropostoList);

		Set<Projeto> projetoPropostoSet = repository.findAllByPrograma(programa);

		if (!projetoPropostoSet.isEmpty()) {
			projetoPropostoSet.forEach(projeto -> {
				if (idProjetoPropostoList.stream()
						.noneMatch(idProjetoProposto -> idProjetoProposto.equals(projeto.getId()))) {
					projeto.setPrograma(null);
				}
			});

			repository.saveAllAndFlush(projetoPropostoSet);
		}

		idProjetoPropostoList.forEach(idProjetoProposto -> {
			Projeto projeto = this.buscar(idProjetoProposto);
			projeto.setPrograma(programa);
			repository.saveAndFlush(projeto);
		});

		logger.info("Projetos vinculados ao programa com sucesso");
		return this.buscarIdProjetoPropostoList(programa);
	}

	@Transactional
	public void desvincularProjetosDoPrograma(Programa programa) {
		logger.info("Desvinculando DIC´s do programa com id: {}", programa.getId());

		Set<Projeto> projetoPropostoSet = repository.findAllByPrograma(programa);

		if (!projetoPropostoSet.isEmpty()) {
			projetoPropostoSet.forEach(projeto -> {
				projeto.setPrograma(null);
			});

			repository.saveAllAndFlush(projetoPropostoSet);
		}

		logger.info("DIC´s desvinculados do programa com sucesso");
	}

	public String gerarNomeArquivo(Integer idProjeto) {
		Projeto projeto = this.buscar(idProjeto.longValue());

		if (projeto.getOrganizacao().getCnpj() == null) {
			throw new RelatorioNomeArquivoException("Organização não possui CNPJ.");
		}

		String cnpj = this.formatarCnpj(projeto.getOrganizacao().getCnpj());

		return "DIC n. " +
				projeto.getCountAno() + "-" +
				projeto.getOrganizacao().getNomeFantasia() + "-" +
				cnpj;
	}

	public int buscarQuantidadeProjetos() {
		return Integer.parseInt(String.valueOf((repository.count())));
	}

	public BigDecimal buscarSomatorioValorEstimadoProjetos() {
		return localidadeQuantiaService.somarValorEstimadoTodosProjetos();
	}

	public Projeto buscar(Long id) {
		return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
	}

	private Long buscarIdResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isResponsavelProponente)
				.findFirst()
				.map(projetoPessoa -> projetoPessoa.getPessoa().getId())
				.orElse(null);
	}

	private String buscarSubResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isResponsavelProponente)
				.findFirst()
				.map(projetoPessoa -> projetoPessoa.getPessoa().getSub())
				.orElse(null);
	}

	private String buscarSubProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isProponente)
				.findFirst()
				.map(projetoPessoa -> projetoPessoa.getPessoa().getSub())
				.orElse(null);
	}

	private String buscarLotacaoResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isResponsavelProponente)
				.findFirst()
				.map(projetoPessoa -> {
					String sub = projetoPessoa.getPessoa().getSub();
					return acessoCidadaoService.buscarNomePapelPrioritarioPorSub(sub);
				})
				.orElse(null);
	}

	private String buscarNomeResponsavelProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isResponsavelProponente)
				.findFirst()
				.map(projetoPessoa -> {
					return projetoPessoa.getPessoa().getNome();
				})
				.orElse(null);
	}

	private String buscarNomeProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isProponente)
				.findFirst()
				.map(projetoPessoa -> projetoPessoa.getPessoa().getNome())
				.orElse("");
	}

	private Long buscarIdProponente(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(ProjetoPessoa::isProponente)
				.findFirst()
				.map(projetoPessoa -> projetoPessoa.getPessoa().getId())
				.orElse(null);
	}

	private List<EquipeDto> buscarEquipeElaboracao(Set<ProjetoPessoa> projetoPessoaSet) {
		return projetoPessoaSet.stream()
				.filter(pessoa -> !pessoa.isResponsavelProponente())
				.map(EquipeDto::new)
				.toList();
	}

	private Set<Projeto> buscarProjetosPorPrograma(Programa programa) {
		return repository.findAllByPrograma(programa);
	}

	private String formatarCnpj(String cnpj) {
		return cnpj.replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$", "$1.$2.$3/$4-$5");
	}

	private String buscarCountAnoFormatado() {
		return FormatadorCountAno.formatar(repository.contagemAnoAtual());
	}

	private void validarProjeto(ProjetoForm form, boolean isSalvar) {
		List<String> erros = new ArrayList<>();

		boolean checkFormIdOrganizacaoExistePorId = !organizacaoService.existePorId(form.idOrganizacao());
		boolean checkProjetoExistePorSigla = repository.existsBySigla(form.sigla()) && isSalvar;

		if (checkFormIdOrganizacaoExistePorId)
			erros.add("Erro ao encontrar Organização com id " + form.idOrganizacao());

		if (checkProjetoExistePorSigla)
			erros.add("Já existe um projeto cadastrado com essa sigla.");

		if (!erros.isEmpty()) {
			erros.forEach(logger::error);
			throw new ValidacaoSiscapException(erros);
		}

	}

	private List<EquipeDto> validarEquipeElaboracao(ProjetoForm form) {

		List<EquipeDto> equipe = new ArrayList<>();

		for (EquipeDto membro : form.equipeElaboracao()) {

			String sub = membro.subPessoa();

			String id = pessoaService.buscarIdPorSub(sub);
			if (id.isBlank()) {
				logger.info("Pessoa com sub [{}] não encontrada na base do SISCAP, procedendo para criação.", sub);
				id = pessoaService.sincronizarAgenteCidadaoPessoaSiscap(sub);
			} else {
				logger.info("Verificar se dados da pessoa com sub [{}] estão batendo com dados da tabela pessoa.", sub);
				pessoaService.sincronizarDadosAgentePessoaSiscap(Long.valueOf(id), sub);
			}

			EquipeDto novoMembro = new EquipeDto(Long.valueOf(id), membro.idPapel(), membro.idStatus(),
					membro.justificativa(), membro.subPessoa(), membro.nome());

			equipe.add(novoMembro);

			logger.info("Verificar se pessoa com id [{}] possui organizacao associada na base do SISCAP.", id);

			List<PessoaOrganizacao> organizacoes = pessoaOrganizacaoService.buscarPorIds(List.of(Long.valueOf(id)));
			if (organizacoes.isEmpty()) {
				logger.info(
						"Pessoa com sub [{}] não possui organizacao associada na base do SISCAP - proceder com atualizacao do AC.",
						sub);
				Set<Organizacao> organizacoesAC = pessoaService.buscarOrganizacoesAssociadas(sub);
				pessoaService.associarOrganizacoesAPessoa(pessoaService.buscarPorSub(sub), organizacoesAC);
			}

		}

		return equipe;

	}

	@Transactional
	public boolean enviarEmailGestorAvaliarDic(Long idProjeto, String subResponsavelProponente, String nomeProponente)
			throws MessagingException, UnsupportedEncodingException {

		if (idProjeto == null || idProjeto == 0) {
			logger.info("ID do projeto não foi informado.");
			return false;
		}

		Projeto projeto = repository.findById(idProjeto)
				.orElse(null);

		if (projeto == null) {
			logger.info("Projeto id [{}] não encontrado.", idProjeto);
			return false;
		}

		Pessoa dadosResponsavelProponente = pessoaService.buscarPorSub(subResponsavelProponente);

		if (dadosResponsavelProponente == null) {
			logger.info("Dados do responsavel proponente sub [{}] não foi encontrado.", subResponsavelProponente);
			return false;
		}

		String emailValido = pessoaService.obterEmailValido(dadosResponsavelProponente);

		List<String> emailsInteressadosList = Arrays.asList(emailValido);

		Long idOrganizacaoProjeto = projeto.getOrganizacao().getId();

		String nomeOrganizacaoProjeto = "";
		
		try {
			OrganizacaoDto organizacaoDto = organizacaoService.buscarPorId(idOrganizacaoProjeto);
			nomeOrganizacaoProjeto = String.format("%s - %s", organizacaoDto.abreviatura(), organizacaoDto.nome());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao buscar dados organizacao projeto.");
		}

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				nomeProponente,
				null,
				nomeOrganizacaoProjeto,
				dadosResponsavelProponente.getNome(),
				emailsInteressadosList,
				projeto.getTitulo(), null);

		boolean confirmacaoEnvioEmail = emailService.enviarEmailAnaliseDIC(envioEmailDicDetalhesDto);

		if (confirmacaoEnvioEmail) {
			logger.info("Email enviado com sucesso");
			return true;
		} else {
			logger.info("Email não foi enviado");
			return true;
		}

	}

	@Transactional
	public boolean enviarEmailPareceresEstrategicoOrcamentario(Long idProjeto, String subResponsavelProponente,
			String nomeProponente) throws MessagingException, UnsupportedEncodingException {

		if (idProjeto == null || idProjeto == 0) {
			logger.info("ID do projeto não foi informado.");
			return false;
		}

		Pessoa dadosResponsavelProponente = pessoaService.buscarPorSub(subResponsavelProponente);

		if (dadosResponsavelProponente == null) {
			logger.info("Dados do responsavel proponente sub [{}] não foi encontrado.", subResponsavelProponente);
			return false;
		}

		Projeto projeto = repository.findById(idProjeto)
				.orElse(null);

		if (projeto == null) {
			logger.info("Projeto id [{}] não encontrado.", idProjeto);
			return false;
		}

		Long idOrganizacaoProjeto = projeto.getOrganizacao().getId();

		String nomeOrganizacaoProjeto = "";
		try {
			OrganizacaoDto organizacaoDto = organizacaoService.buscarPorId(idOrganizacaoProjeto);
			nomeOrganizacaoProjeto = String.format("%s - %s", organizacaoDto.abreviatura(), organizacaoDto.nome());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao buscar dados organizacao projeto.");
		}

		EnvioEmailDetalhesDto envioEmailDicDetalhesDto = new EnvioEmailDetalhesDto(idProjeto,
				nomeProponente,
				null,
				nomeOrganizacaoProjeto,
				dadosResponsavelProponente.getNome(),
				null,
				projeto.getTitulo(), null);

		boolean confirmacaoEnvioEmail = emailService
				.enviarEmailPareceresEstrategicoOrcamentario(envioEmailDicDetalhesDto);

		if (confirmacaoEnvioEmail) {
			logger.info("Email enviado com sucesso");
			return true;
		} else {
			logger.info("Email não foi enviado");
			return true;
		}

	}

	@Transactional
	public void atualizarIdArquivoCapturadoProcessoEdocsProjeto(Long idProjeto, String idArquivoCapturado) {
		Projeto projeto = this.buscar(idProjeto);

		projeto.setIdDocumentoCapturadoEdocs(idArquivoCapturado);

		repository.save(projeto);
	}

	@Transactional
	public void atualizarIdProcessoEdocsProjeto(Long idProjeto, String idProcessoEdocs) {
		Projeto projeto = this.buscar(idProjeto);

		projeto.setIdProcessoEdocs(idProcessoEdocs);

		repository.save(projeto);
	}

	public boolean subEhResponsavelProponenteProjeto(String subUsuario, Long idProjeto) {

		Projeto projeto = this.buscar(idProjeto);

		Optional<Pessoa> responsavelProponenteProjeto = projeto.getProjetoPessoaSet()
				.stream()
				.filter(membro -> membro.isResponsavelProponente())
				.findFirst()
				.map(proponente -> proponente.getPessoa());

		return responsavelProponenteProjeto
				.map(pessoa -> pessoa.getSub().equalsIgnoreCase(subUsuario))
				.orElse(false);

	}

}