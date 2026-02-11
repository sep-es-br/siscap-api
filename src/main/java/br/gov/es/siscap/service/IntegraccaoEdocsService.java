package br.gov.es.siscap.service;

import br.gov.es.siscap.client.EdocsWebClient;
import br.gov.es.siscap.dto.ProgramaDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACUserInfoDto;
import br.gov.es.siscap.dto.edocswebapi.*;
import br.gov.es.siscap.enums.StatusParecerEnum;
import br.gov.es.siscap.enums.StatusProjetoEnum;
import br.gov.es.siscap.enums.edocs.ContextoIntegracaoEdocsEnum;
import br.gov.es.siscap.enums.edocs.EtapasIntegracaoEdocsEnum;
import br.gov.es.siscap.enums.edocs.SituacaoEventoEdocsEnum;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoParecer;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegraccaoEdocsService {

	@Value("${api.edocs.classedocumento-id}")
	private String classeDocumentoId;

	@Value("${api.edocs.guiddestinoSUBCAP}")
	private String guiddestinoSUBCAP;

	private final EdocsWebClient EdocsWebClient;
	private final AcessoCidadaoAutorizacaoService AutorizacaoACService;
	private final AcessoCidadaoService AcessoCidadaoService;
	private final UploadS3Service UploadS3Service;
	private final ProjetoService projetoService;
	private final AutenticacaoService autenticacaoService;
	private final RelatoriosService relatoriosService;
	private final ProjetoParecerService projetoParecerService;

	private final Logger logger = LogManager.getLogger(IntegraccaoEdocsService.class);

	private Map<ChaveEtapasIntegracao, List<EtapasIntegracaoDto>> etapasPorChave = new HashMap<>();

	public void atualizarEtapa(ChaveEtapasIntegracao chave, EtapasIntegracaoEdocsEnum etapaEnum, boolean iniciou,
			boolean finalizou) {
		List<EtapasIntegracaoDto> etapas = etapasPorChave.get(chave);
		if (etapas != null) {
			for (EtapasIntegracaoDto etapa : etapas) {
				logger.info("Etapa atualizada : {}", etapa.getEtapa().name());
				if (etapa.getEtapa().equals(etapaEnum)) {
					etapa.setIniciou(iniciou);
					etapa.setFinalizou(finalizou);
					etapa.setErro(false);
					break;
				}
			}
		}
	}

	public void finalizaTodasEtapas(ChaveEtapasIntegracao chave) {
		List<EtapasIntegracaoDto> etapas = etapasPorChave.get(chave);
		if (etapas != null) {
			for (EtapasIntegracaoDto etapa : etapas) {
				etapa.setIniciou(true);
				etapa.setFinalizou(true);
				etapa.setErro(false);
			}
		}
	}

	public void registrarFalhaEtapa(ChaveEtapasIntegracao chave, EtapasIntegracaoEdocsEnum etapaEnum) {
		List<EtapasIntegracaoDto> etapas = etapasPorChave.get(chave);
		if (etapas != null) {
			for (EtapasIntegracaoDto etapa : etapas) {
				if (etapa.getEtapa().equals(etapaEnum)) {
					etapa.setIniciou(false);
					etapa.setFinalizou(false);
					etapa.setErro(true);
					break;
				}
			}
		}
	}

	public void registrarFalhaEtapa(ChaveEtapasIntegracao chave, EtapasIntegracaoEdocsEnum etapaEnum,
			String msgAlerta) {
		List<EtapasIntegracaoDto> etapas = etapasPorChave.get(chave);
		if (etapas != null) {
			for (EtapasIntegracaoDto etapa : etapas) {
				if (etapa.getEtapa().equals(etapaEnum)) {
					etapa.setIniciou(false);
					etapa.setFinalizou(false);
					etapa.setErro(true);
					etapa.setMsgAlertaExibir(msgAlerta);
					break;
				}
			}
		}
	}

	public void limparEtapas(ChaveEtapasIntegracao chave) {
		etapasPorChave.remove(chave);
	}

	public void adicionarEtapa(ChaveEtapasIntegracao chave, EtapasIntegracaoDto etapa) {
		logger.info("Etapa adicionada : {}", etapa.getEtapa().name());
		etapasPorChave
				.computeIfAbsent(chave, k -> new ArrayList<>())
				.add(etapa);
	}

	public void assinarAutuarDespacharDicProccessoSUBCAP(Resource arquivoDic, String nomeArquivo, Long idProjeto) {

		logger.info("Iniciando processo para Autuacao/Despacho do projeto {} para SUBCAP..", idProjeto);

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.limparEtapas(chave);

		ProjetoDto projetoDtoIntegrando = projetoService.buscarPorId(idProjeto);

		autuarDicProjetoReativo(projetoDtoIntegrando, arquivoDic, nomeArquivo)
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));

		return;

	}

	public void assinarCapturaParecerDIC(Long idProjeto, Long idParecer) {

		logger.info("Iniciando processo para Assinar e Capturar Pareceres do projeto {} no E-Docs..", idProjeto);

		if (projetoParecerService.verificarCapturaParecer(idParecer)) {
			logger.info("Parecere {} já capturado no E-Docs..", idParecer);
			throw new ValidacaoSiscapException(
					List.of("Parecer já capturado via E-Docs"));
		}

		String subUsuarioLogado = autenticacaoService.getUsuarioLogado();

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.limparEtapas(chave);

		Resource resource = relatoriosService.gerarArquivoParecerDIC("PARECER", idProjeto, idParecer,
				projetoParecerService.buscarTipoParecer(idParecer));
		String nomeArquivo = projetoParecerService.gerarNomeArquivoParecerDIC(idParecer);

		ProjetoDto projetoDto = projetoService.buscarPorId(idProjeto);// new ProjetoDto(projeto);

		String subJwt = autenticacaoService.getUsuarioSub();

		this.assinarCapturarParecerProjetoReativo(projetoDto, resource, nomeArquivo, idParecer, subUsuarioLogado)
				.flatMap(mensagem -> {
					logger.info("SUCESSO: {}", mensagem);
					if (projetoParecerService.buscarTipoParecer(idParecer).equals("GEOC")) {
						return this.entranharParecerProcesso(projetoDto, idParecer, subJwt);
					} else {
						return Mono.empty();
					}
				})
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.error("ERRO: {}", erro));

	}

	public void despacharProccessoEdocsOrgaoOrigem(Long idProjeto, List<ProjetoCamposComplementacaoDto> complementos) {

		logger.info("Iniciando processo para despachar processo E-Docs DIC do projeto {} para Orgao de Origem..",
				idProjeto);

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.limparEtapas(chave);

		ProjetoDto projetoDto = projetoService.buscarPorId(idProjeto);

		this.despacharProcessoEdcosDicComplementarReativo(projetoDto)
				.doOnSuccess(
						retorno -> projetoService.enviarAvisoSolicitarComplementacaoProjeto(idProjeto, complementos))
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));

		return;
	}

	public void encerrarProcessoEdocs(ProjetoDto projetoDto) {
		logger.info("Iniciando processo para encerramento processo do E-Docs referente ao DIC {} .", projetoDto.id());
		this.encerrarProcessoEdcosReativo(projetoDto)
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));
		return;
	}

	public void reentranharDespacharDicProccessoComplementacaoSUBCAP(Resource arquivoDic, String nomeArquivo,
			Long idProjeto) {

		logger.info("Iniciando processo para reentranhamento DIC complementado do projeto {} para SUBCAP..", idProjeto);

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.limparEtapas(chave);

		ProjetoDto projetoDto = projetoService.buscarPorId(idProjeto);

		reentranharDicProjetoReativo(projetoDto, arquivoDic, nomeArquivo)
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));

		return;

	}

	public void despacharProcessoEdocsDICComplementacao(Resource arquivoDic, String nomeArquivo, Long idProjeto) {

		logger.info("Iniciando processo para despachar processo no E-Docs do DIC que deve ser complementado.",
				idProjeto);

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.limparEtapas(chave);

		ProjetoDto projetoDtoIntegrando = projetoService.buscarPorId(idProjeto);

		this.despacharProcessoEdcosDicComplementarReativo(projetoDtoIntegrando)
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));

		return;

	}

	public Mono<String> despacharProcessoEdcosDicComplementarReativo(ProjetoDto projetoDto) {

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, false,
						false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> {
					try {
						if (!this.validarMovimentacaoProcessoEdcos(token, projetoDto.idProcessoEdocs())) {
							String msgAlerta = "Não é possível despachar o processo pois o mesmo está em um local de custódia que impede essa movimentação no E-Docs por você.";
							this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO,
									msgAlerta);
							throw new ValidacaoSiscapException(List.of(msgAlerta));
						}
						return new FluxoContextoIntegracaoDto(projetoDto, token);
					} catch (Exception e) {
						this.registrarFalhaEtapa(
								chave,
								EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
						return null;
					}
				})
				.flatMap(ctx -> despacharProcessoDICOrgaoOrigem(ctx))
				.flatMap(ctx -> consultarSituacaoDespachar(ctx))
				.doOnSuccess(retornoSituacaoDespacho -> {
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, true);
				})
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para despachar o processo via E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
				})
				.thenReturn("Despachar processo de DIC para orgão de origem finalizado com sucesso.");

	}

	public Mono<String> encerrarProcessoEdcosReativo(ProjetoDto projetoDto) {

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("", erroBuscarToken);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(projetoDto, token))
				.flatMap(ctx -> encerrarProcessoEdocs(ctx))
				.flatMap(ctx -> consultarSituacaoEncerramento(ctx))
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para ENCERRAR o processo via E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
				})
				.thenReturn("Encerramento do processo no Edocs realizado com sucesso.");
	}

	public Mono<String> autuarDicProjetoReativo(ProjetoDto projetoDto, Resource arquivo, String nomeArquivo) {

		final long tamanho;
		try {
			tamanho = arquivo.contentLength();
		} catch (IOException e) {
			return Mono.error(new RuntimeException("Falha ao obter tamanho do arquivo", e));
		}

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false));

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.AUTUAR, false, false, false));

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, false, false,
						false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(projetoDto, token))
				.flatMap(ctx -> gerarUrlUpload(ctx, tamanho))
				.flatMap(ctx -> uploadArquivo(ctx, arquivo, nomeArquivo))
				.flatMap(ctx -> capturarAssinar(ctx, nomeArquivo))
				.flatMap(ctx -> consultarSituacaoCaptura(ctx))
				.flatMap(ctx -> autuarProcessoMono(ctx))
				.flatMap(ctx -> consultarSituacaoEventoAtuacao(ctx))
				.flatMap(ctx -> despacharProcessoDIC(ctx))
				.flatMap(ctx -> consultarSituacaoDespachar(ctx))
				.flatMap(ctx -> atualizarProjeto(ctx))
				.doOnSuccess(retorno -> {
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, true);
					projetoService.enviarEmailGerenciaSubcapDicAutuado(projetoDto.id());
				})
				.thenReturn("Atuação concluída com sucesso.");

	}

	public Mono<String> assinarCapturarParecerProjetoReativo(ProjetoDto projetoDto, Resource arquivo,
			String nomeArquivo, Long idParecer, String subUsuarioLogado) {

		final long tamanho;
		try {
			tamanho = arquivo.contentLength();
		} catch (IOException e) {
			return Mono.error(new RuntimeException("Falha ao obter tamanho do arquivo", e));
		}

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(projetoDto, token))
				.flatMap(ctx -> gerarUrlUpload(ctx, tamanho))
				.flatMap(ctx -> uploadArquivo(ctx, arquivo, nomeArquivo))
				.flatMap(ctx -> capturarAssinar(ctx, nomeArquivo))
				.flatMap(ctx -> consultarSituacaoCaptura(ctx))
				.doOnSuccess(retorno -> finalizaTodasEtapas(chave))
				.flatMap(ctx -> atualizarParecer(ctx, idParecer, subUsuarioLogado))
				.doOnSubscribe(sub -> logger.info("Iniciando atualização do parecer {}", idParecer))
				.doOnSuccess(v -> logger.info("Parecer {} atualizado com sucesso", idParecer))
				.doOnError(e -> logger.error("Erro ao atualizar parecer {}", idParecer, e))
				.thenReturn("Assinatura e Captura do parecer concluída com sucesso.");

	}

	public Mono<String> reentranharDicProjetoReativo(ProjetoDto projetoDto, Resource arquivoCorrigido,
			String nomeArquivo) {

		final long tamanho;
		try {
			tamanho = arquivoCorrigido.contentLength();
		} catch (IOException e) {
			return Mono.error(new RuntimeException("Falha ao obter tamanho do arquivo", e));
		}

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false));

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO, false, false,
						false));

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.DESENTRANHAR, false, false, false));

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(projetoDto.id(), EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, false, false,
						false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> {
					try {
						if (!this.validarMovimentacaoProcessoEdcos(token, projetoDto.idProcessoEdocs())) {
							String msgAlerta = "Não é possível realizar o reentramento porque o processo está em um local de custódia que impede essa movimentação no E-Docs por você.";
							this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
									msgAlerta);
							throw new ValidacaoSiscapException(List.of(msgAlerta));
						}
						return new FluxoContextoIntegracaoDto(projetoDto, token);
					} catch (Exception e) {
						this.registrarFalhaEtapa(
								chave,
								EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
						return null;
					}

				})
				.flatMap(ctx -> gerarUrlUpload(ctx, tamanho))
				.flatMap(ctx -> uploadArquivo(ctx, arquivoCorrigido, nomeArquivo))
				.flatMap(ctx -> capturarAssinar(ctx, nomeArquivo))
				.flatMap(ctx -> consultarSituacaoCaptura(ctx))
				.flatMap(ctx -> entranharDocumentoEdocs(ctx))
				.flatMap(ctx -> consultarSituacaoEntranhamento(ctx))

				.flatMap(ctx -> processosVinculadosDocumento(ctx))
				.flatMap(ctx -> atosVinculadosProcesso(ctx))
				.flatMap(ctx -> documentosAtosProcesso(ctx))
				.flatMap(ctx -> desentranharDocumento(ctx))
				.flatMap(ctx -> consultarSituacaoDesentranhamento(ctx))

				.flatMap(ctx -> despacharProcessoDIC(ctx))
				.flatMap(ctx -> consultarSituacaoDespachar(ctx))
				.flatMap(ctx -> atualizarProjeto(ctx))

				.doOnSuccess(retorno -> this.finalizaTodasEtapas(chave))
				.thenReturn("Reentranhamento de DIC complementado concluída com sucesso.");

	}

	private Mono<FluxoContextoIntegracaoDto> entranharDocumentoEdocs(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar processo de entranhamento do documento ao processo E-Docs - ID {}", ctx.getProjeto().id());
		logger.info("ID documento a ser entranhado {}", ctx.getIdDocumentos()[0]);

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> entranharDocumentosProcessoEdocs(
				ctx.getProjeto().idProcessoEdocs(),
				ctx.getIdDocumentos(),
				ctx.getProjeto().subResponsavelProponente(),
				ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(
						Mono.error(new RuntimeException("Falha ao consultar situacao do evento de entranhamento.")))
				.doOnSuccess(retorno -> ctx.setIdEventoEntranhamento(retorno.replace("\"", "")))
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para entranhamento de um documento via E-Docs.",
							e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO);
				})
				.thenReturn(ctx);
	}

	private Mono<String> atualizarParecer(FluxoContextoIntegracaoDto ctx, Long idParecer, String subUsuarioLogado) {

		return FeignReativo.fromFeign(() -> consultarDadosArquivoCapturado(ctx.getIdDocumentos()[0], ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao executar chamada ao endpoint para consultar dados de um documento via E-Docs.")))
				.flatMap(dadosArquivo -> {

					String codigoRegistroEdocs = dadosArquivo.registro();

					return Mono.fromCallable(() -> {

						projetoParecerService.atualizarIdArquivoCapturado(
								ctx.getIdDocumentos()[0],
								idParecer,
								subUsuarioLogado,
								codigoRegistroEdocs);

						if (projetoParecerService.verificarEnvioPareceresProjeto(ctx.getProjeto().id())) {
							projetoService.alterarStatusProjeto(
									ctx.getProjeto().id(),
									StatusProjetoEnum.ELEGIBILIDADE.getValue());

							projetoParecerService.enviarAvisoPareceresProjetoCapturadosEdocs(
									ctx.getProjeto().id(),
									ctx.getProjeto().sigla());
						}

						return "Atualização do parecer concluída com sucesso.";

					});
				})
				.doOnError(e -> logger.error("Erro ao atualizar parecer com dados do E-Docs", e));

	}

	private Mono<String> atualizarProjeto(FluxoContextoIntegracaoDto ctx) {

		String idProjetoEDocs = (ctx.getIdProcesso() != null && !ctx.getIdProcesso().isEmpty()) ? ctx.getIdProcesso()
				: ctx.getProjeto().idProcessoEdocs();

		return FeignReativo.fromFeign(() -> consultarDadosProcessoEdocs(
				idProjetoEDocs,
				ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao executar chamada ao endpoint para consultar dados do processo via E-Docs.")))
				.doOnSuccess(retornoDadosProcesso -> {

					logger.info("Gravando Protocolo do processo E-Docs {} no processo do SISCAP.",
							retornoDadosProcesso.protocolo());

					if (retornoDadosProcesso.protocolo() != null && !retornoDadosProcesso.protocolo().isEmpty())
						projetoService.atualizarProtocoloProcessoEdocsProjeto(ctx.getProjeto().id(),
								retornoDadosProcesso.protocolo());

					if (ctx.getIdDocumentos() != null && !ctx.getIdDocumentos()[0].isEmpty())
						projetoService.atualizarIdArquivoCapturadoProcessoEdocsProjeto(ctx.getProjeto().id(),
								ctx.getIdDocumentos()[0]);

					if (idProjetoEDocs != null && !idProjetoEDocs.isEmpty())
						projetoService.atualizarIdProcessoEdocsProjeto(ctx.getProjeto().id(), idProjetoEDocs);

				})
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para consultar dados de um processo no E-Docs.",
							e);
				})
				.thenReturn("Atualização DIC complementado concluída com sucesso.");

	}

	private Mono<FluxoContextoIntegracaoDto> despacharProcessoDICOrgaoOrigem(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar processo de despachar processo E-Docs DIC Id: {}.", ctx.getProjeto().id());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> despacharProcessoOrgaoOrigem(ctx))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(
						Mono.error(new RuntimeException(
								"Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.")))
				.doOnSuccess(retorno -> ctx.setIdEventoDespachar(retorno.replace("\"", "")))
				.doOnError(e -> {
					logger.error(
							"Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> encerrarProcessoEdocs(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar processo de encerramento processo E-Docs DIC Id: {}.", ctx.getProjeto().id());

		return FeignReativo.fromFeign(() -> encerrarProcessoEdcosClient(ctx))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(
						Mono.error(new RuntimeException(
								"Falha ao executar chamada ao endpoint para encerrar um processo via E-Docs.")))
				.doOnSuccess(retorno -> ctx.setIdEventoEncerramento(retorno.replace("\"", "")))
				.doOnError(e -> {
					logger.error(
							"Falha ao executar chamada ao endpoint para encerramento um processo via E-Docs.", e);
				})
				.thenReturn(ctx);

	}

	private Mono<FluxoContextoIntegracaoDto> despacharProcessoDIC(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar processo de despachar processo E-Docs DIC Id: {}.", ctx.getProjeto().id());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> despacharProcessoSUBCAP(ctx))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(
						Mono.error(new RuntimeException(
								"Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.")))
				.doOnSuccess(retorno -> ctx.setIdEventoDespachar(retorno.replace("\"", "")))
				.doOnError(e -> {
					logger.error(
							"Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
				})
				.thenReturn(ctx);

	}

	private Mono<FluxoContextoIntegracaoDto> processosVinculadosDocumento(FluxoContextoIntegracaoDto ctx) {

		String idDocumentoEDocs = Optional.ofNullable(ctx.getIdDocumentoDesentranhar())
				.filter(s -> !s.isEmpty())
				.orElseGet(() -> Optional.ofNullable(ctx.getProjeto())
						.map(p -> p.idDocumentoDicEdocs())
						.orElse(""));

		if (idDocumentoEDocs == null || idDocumentoEDocs.isEmpty()) {
			logger.error(
					"Falha ao consultar lista de processos vinculados ao documento a ser desentranhado - ID do documento no Edocs nao informado.");
		}

		logger.info(
				"Iniciar consulta processos vinculados ao documento. {}",
				idDocumentoEDocs);

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> consultarProcessosEdocsVinculadosDocumento(
				idDocumentoEDocs,
				ctx.getToken()))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(
						Mono.error(new RuntimeException("Falha ao consultar processos vinculados ao documento.")))
				.doOnRequest(n -> this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR,
						true, false))
				.doOnError(e -> {
					logger.error(
							"Falha ao consultar lista de processos vinculados ao documento a ser desentranhado.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
				})
				.flatMap(listaProcessosVinculados -> Mono.justOrEmpty(
						listaProcessosVinculados.stream()
								.filter(processo -> processo.protocolo().equals(ctx.getProjeto().protocoloEdocs()))
								.findFirst()))
				.doOnSuccess(processoVinculado -> ctx.setDtoProcessoVinculadoDocumento(processoVinculado))
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> atosVinculadosProcesso(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta atos vinculados ao processo. {}", ctx.getProjeto().idProcessoEdocs());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo
				.fromFeign(() -> consultarAtosProcessoEdocs(ctx.getProjeto().idProcessoEdocs(), ctx.getToken()))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(new RuntimeException("Falha ao consultar atos do processo vinculado.")))
				.doOnError(e -> {
					logger.error("Falha ao consultar atos do processo vinculado.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
				})
				.flatMap(atosProcesso -> Mono.justOrEmpty(atosProcesso.stream()
						.filter(ato -> ato.tipo() == 1) // tipo AUTUACAO..
						.findFirst()))
				.doOnSuccess(atoEntranhamento -> {
					ctx.setDtoAtoProcessoDocs(atoEntranhamento);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> documentosAtosProcesso(FluxoContextoIntegracaoDto ctx) {

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		if (ctx.getProjeto() == null || ctx.getProjeto().idProcessoEdocs() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		logger.info("Iniciar consulta documentos ato ao processo. ID do Processo no Edocs: {}",
				ctx.getProjeto().idProcessoEdocs());

		if (ctx.getToken() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		if (ctx.getIdDocumentos() == null && ctx.getIdDocumentoDesentranhar() == null
				&& ctx.getProjeto().idDocumentoDicEdocs() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		String idDocumentoEDocs = Optional.ofNullable(ctx.getIdDocumentoDesentranhar())
				.filter(s -> !s.isEmpty())
				.orElseGet(() -> Optional.ofNullable(ctx.getProjeto())
						.map(p -> p.idDocumentoDicEdocs())
						.orElse(""));

		if (idDocumentoEDocs == null || idDocumentoEDocs.isEmpty()) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		if (ctx.getProjeto() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		if (ctx.getDtoAtoProcessoDocs() == null || ctx.getDtoAtoProcessoDocs().id() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			return null;
		}

		return FeignReativo.fromFeign(() -> consultarDocumentosAtoProcesso(
				ctx.getProjeto().idProcessoEdocs(),
				ctx.getDtoAtoProcessoDocs().id(),
				ctx.getToken()))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(new RuntimeException("Falha ao consultar documentos do ato do processo.")))
				.doOnError(e -> {
					logger.error("Erro ao consultar documentos do ato do processo.", e);
					registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
				})
				.map(documentos -> {

					documentos.stream()
							.filter(doc -> {
								logger.error("Documento vinculado ao projeto : ", idDocumentoEDocs);
								return doc.documentoId().equals(idDocumentoEDocs);
							})
							.findFirst()
							.ifPresent(ctx::setDocumentoAtoProcessoDto);

					return ctx;

				});
	}

	private Mono<FluxoContextoIntegracaoDto> desentranharDocumento(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar desentranhamento documento ID {}", ctx.getProjeto().idDocumentoDicEdocs());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		if (ctx.getToken() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			throw new ValidacaoSiscapException(List.of("Token não informado no contexto."));
		}

		if (ctx.getProjeto() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			throw new ValidacaoSiscapException(List.of("Projeto não encontrado no contexto."));
		}

		if (ctx.getDocumentoAtoProcessoDto() == null || ctx.getDocumentoAtoProcessoDto().sequencial() == null) {
			this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
			throw new ValidacaoSiscapException(List.of("Sequencial do documento não encontrado."));
		}

		return FeignReativo.fromFeign(() -> desentranharDocumentoProcessoEdocs(
				ctx.getProjeto().idProcessoEdocs(),
				ctx.getDocumentoAtoProcessoDto().sequencial().toString(),
				ctx.getProjeto().subResponsavelProponente(),
				ctx.getToken()))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar o desentranhamento do documento.")))
				.doOnSuccess(retorno -> ctx.setIdEventoDesentranhar(retorno.replace("\"", "")))
				.doOnError(e -> {
					logger.error("Falha ao executar o desentranhamento do documento.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoEventoAtuacao(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento AUTUACAO id {}.", ctx.getIdEventoAutuar());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> consultarSituacaoEventoEdocs(ctx.getIdEventoAutuar(), ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono
						.error(new RuntimeException("Falha ao consultar situacao do evento de AUTUACAO DO PROCESSO ID "
								+ ctx.getIdEventoAutuar() + ".")))
				.doOnRequest(
						n -> this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR, true, true))
				.doOnError(e -> {
					logger.error("Falha ao verificar situacao do evento de autuacao do processo no E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR);
				})
				.doOnSuccess(resultConsultaEvento -> {
					ctx.setIdProcesso(resultConsultaEvento.idProcesso());
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR, true, true);
				})
				.thenReturn(ctx);

	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoCaptura(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento - CAPTURA - id {}.", ctx.getIdEventoCaptura());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> consultarSituacaoEventoEdocs(ctx.getIdEventoCaptura(), ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono
						.error(new RuntimeException("Falha ao consultar situacao do evento de CAPTURA DO PROCESSO ID "
								+ ctx.getIdEventoCaptura() + ".")))
				.doOnError(e -> {
					logger.error("Falha ao verificar situacao do evento de autuacao do processo no E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
				})
				.doOnSuccess(resultConsultaEvento -> {
					ctx.setIdDocumentos(new String[] { resultConsultaEvento.idDocumento() });
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, true);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoEntranhamento(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento id {}.", ctx.getIdEventoEntranhamento());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo
				.fromFeign(() -> EdocsWebClient.buscarSituacaoEvento(ctx.getToken(), ctx.getIdEventoEntranhamento()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(
						Mono.error(new RuntimeException(
								"Falha ao consultar situacao evento de ENTRANHAMENTO via E-Docs.")))
				.doOnSuccess(resultConsultaEvento -> {
					ctx.setSituacaoEventoEntranhamentoDto(resultConsultaEvento);
					this.atualizarEtapa(
							chave,
							EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO,
							true,
							true);
				})
				.doOnError(e -> {
					logger.error("Falha ao consultar situacao evento de ENTRANHAMENTO via E-Docs.", e);
					registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoDesentranhamento(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento DESENTRANHAMENTO id {}.", ctx.getIdEventoDesentranhar());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo
				.fromFeign(() -> EdocsWebClient.buscarSituacaoEvento(ctx.getToken(), ctx.getIdEventoDesentranhar()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(
						new RuntimeException("Falha ao consultar situcao evento de DESENTRANHAMENTO via E-Docs.")))
				.doOnSuccess(resultConsultaEvento -> {
					ctx.setSituacaoEventoEntranhamentoDto(resultConsultaEvento);
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR, true, true);
				})
				.doOnError(e -> {
					logger.error("Falha ao consultar situacao evento de DESENTRANHAMENTO via E-Docs.", e);
					registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESENTRANHAR);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoDespachar(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento - DESPACHAR - id {}.", ctx.getIdEventoDespachar());

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> consultarSituacaoEventoEdocs(ctx.getIdEventoDespachar(), ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono
						.error(new RuntimeException("Falha ao consultar situacao do evento de DESPACHO do processo ID "
								+ ctx.getIdEventoDespachar() + ".")))
				.doOnRequest(n -> this.atualizarEtapa(chave,
						EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, false))
				.doOnError(e -> {
					logger.error("Falha ao verificar situacao do evento de despacho do processo no E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoEncerramento(FluxoContextoIntegracaoDto ctx) {
		logger.info("Iniciar consulta situacao evento - ENCERRAMENTO - id {}.", ctx.getIdEventoEncerramento());
		return FeignReativo.fromFeign(() -> consultarSituacaoEventoEdocs(ctx.getIdEventoEncerramento(), ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(
						new RuntimeException("Falha ao consultar situacao do evento de ENCERRAMENTO do processo ID "
								+ ctx.getIdEventoEncerramento() + ".")))
				.doOnError(e -> {
					logger.error("Falha ao verificar situacao do evento de encerramento do processo no E-Docs.", e);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> autuarProcessoMono(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciando autuacao do processo referente ao DIC no E-Docs.");

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> autuarProcesso(
				ctx.getProjeto(),
				ctx.getToken(),
				ctx.getIdDocumentos()[0]))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao executar chamada ao endpoint para autuar um processo via E-Docs.")))
				.doOnSuccess(IdEventoComandoAutuacao -> {
					logger.info("Autuacao foi comandada no E-Docs - ID {}", IdEventoComandoAutuacao);
					ctx.setIdEventoAutuar(IdEventoComandoAutuacao.replace("\"", ""));
				})
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs. {}", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR);
				})
				.thenReturn(ctx);

	}

	private Mono<FluxoContextoIntegracaoDto> capturarAssinar(FluxoContextoIntegracaoDto ctx, String nomeArquivo) {

		logger.info("Iniciando o processo de capturar/assinar DIC - E-Docs.");

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> capturarAssinarDocumento(
				ctx.getDtoUploadArquivoResponse().identificadorTemporarioArquivoNaNuvem(),
				nomeArquivo,
				ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException("Falha ao capturar/assinar documento via E-Docs.")))
				.doOnSuccess(IdEventoRetornoCaptura -> {
					logger.info("Captura realizada: {}", IdEventoRetornoCaptura);
					ctx.setIdEventoCaptura(IdEventoRetornoCaptura.replace("\"", ""));
				})
				.doOnError(e -> {
					logger.error("Falha ao enviar captura do arquivo para o servidor S3 do E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> uploadArquivo(FluxoContextoIntegracaoDto ctx, Resource arquivo,
			String nomeArquivo) {

		logger.info("Iniciando o processo de upload arquivo para o S3 - E-Docs.");

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> UploadS3Service.enviarArquivoParaS3OkHttp(
				ctx.getDtoUploadArquivoResponse().url(),
				ctx.getDtoUploadArquivoResponse().body(),
				arquivo,
				nomeArquivo,
				ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(
						new RuntimeException("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.")))
				.doOnSuccess(retornoEnvio -> {
					logger.info("Upload feito com sucesso: {}", retornoEnvio);
				})
				.doOnError(e -> {
					logger.error("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> gerarUrlUpload(FluxoContextoIntegracaoDto ctx, long tamanho) {

		logger.info("Iniciando o processo de upload arquivo para o E-Docs.");

		var chave = new ChaveEtapasIntegracao(ctx.getProjeto().id(), ContextoIntegracaoEdocsEnum.DIC);

		return FeignReativo.fromFeign(() -> EdocsWebClient.gerarUrlUploadArquivo(ctx.getToken(), tamanho))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(
						Mono.error(new RuntimeException("Falha na geração da URL temporária para Upload do Arquivo.")))
				.doOnRequest(n -> this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
						true, false))
				.doOnSuccess(urlDto -> {
					logger.info("URL gerada: {}", urlDto.toString());
					ctx.setDtoUploadArquivoResponse(urlDto);
				})
				.doOnError(e -> {
					logger.error("Falha ao gerar URL", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
				})
				.thenReturn(ctx);
	}

	private Mono<String> buscarTokenReativo() {

		String subJwt = autenticacaoService.getUsuarioSub();
		String tokenArmazenado = AutorizacaoACService.getEdocsToken(subJwt);

		return Mono.fromCallable(() -> {
			return EdocsWebClient.buscarPapeisUsuarioEdocs(tokenArmazenado);
		})
				.flatMap(listaPapeis -> {
					if (listaPapeis == null || listaPapeis.isEmpty()) {
						return Mono.error(new ValidacaoSiscapException(List.of(
								"O token do E-Docs é válido, mas o usuário não possui papéis ativos. " +
										"Realize um novo login no SISCAP para restaurar a sessão.")));
					}
					return Mono.just(tokenArmazenado);
				})
				.onErrorResume(WebClientResponseException.Unauthorized.class,
						ex -> Mono.error(new ValidacaoSiscapException(List.of(
								"O token do E-Docs expirou. Realize um novo login no SISCAP."))));

	}

	private Mono<String> buscarTokenReativo(String subJwt) {
		return Mono.fromSupplier(() -> AutorizacaoACService.getEdocsToken(subJwt));
	}

	private SituacaoEventoDto consultarSituacaoEventoEdocs(String idEventoEdocs, String token) {
		logger.info("Iniciar consulta situacao evento id {}.", idEventoEdocs);
		return EdocsWebClient.buscarSituacaoEvento(token, idEventoEdocs);
	}

	private ProcessoEdocsDto consultarDadosProcessoEdocs(String idProcessoEdocs, String token) {
		logger.info("Iniciar consulta dados do processo E-Docs id {}.", idProcessoEdocs);
		return EdocsWebClient.buscarDadosProcessoEdocs(token, idProcessoEdocs);
	}

	private String capturarAssinarDocumento(String identificadorTemporarioArquivo, String nomeArquivo, String token) {

		String tokenLimpo = token.replace("Bearer ", "").trim();

		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());
		String guidPapelUsuario = listaPapeisUsuario.stream()
				.filter(papel -> papel.Prioritario())
				.findFirst()
				.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null))
				.Guid();

		String idPapelCapturadorAssinante = guidPapelUsuario;

		String idClasse = this.classeDocumentoId;

		boolean credenciarCapturador = true;

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);
		CapturaAssinaturaBodyDto capturaAssinaturaBodyDto = new CapturaAssinaturaBodyDto(idPapelCapturadorAssinante,
				idClasse,
				nomeArquivo, credenciarCapturador,
				restricaoAcessoBodyDto,
				identificadorTemporarioArquivo);

		return EdocsWebClient.capturarDocumento(token, capturaAssinaturaBodyDto);

	}

	private String autuarProcesso(ProjetoDto projetoDTO, String token, String idDocumentoCapturado) {

		logger.info("Iniciar autuacao do processo para o projeto id {} - documento id {}.", projetoDTO.id(),
				idDocumentoCapturado);

		String idClasse = classeDocumentoId;

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(projetoDTO.subResponsavelProponente());

		String idPapelResponsavel = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::Guid)
						.orElse(""));

		String idLocal = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::LotacaoGuid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::LotacaoGuid)
						.orElse(""));

		String resumo = String.format("AUTUAÇÃO PROJETO - %s", projetoDTO.titulo());

		List<String> idsAgentesInteressados = projetoDTO.equipeElaboracao()
				.stream()
				.map(membro -> membro.subPessoa())
				.collect(Collectors.toList());

		Optional.ofNullable(projetoDTO.subResponsavelProponente())
				.filter(v -> !idsAgentesInteressados.contains(v))
				.ifPresent(idsAgentesInteressados::add);

		Optional.ofNullable(projetoDTO.subProponente())
				.filter(v -> !idsAgentesInteressados.contains(v))
				.ifPresent(idsAgentesInteressados::add);

		List<String> idsDocumentosEntranhados = List.of(idDocumentoCapturado);

		AutuarProjetoDto autuarProjetoDto = new AutuarProjetoDto(idClasse, idPapelResponsavel, idLocal, resumo,
				idsAgentesInteressados, idsDocumentosEntranhados);

		return EdocsWebClient.autuarProcesso(token, autuarProjetoDto);

	}

	private String despacharProcessoSUBCAP(FluxoContextoIntegracaoDto ctx) {

		String idDestino = guiddestinoSUBCAP;

		String mensagem = "Despacho gerado via sistema de captação - SISCAP";

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(ctx.getProjeto().subResponsavelProponente());

		String idPapelResponsavel = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::Guid)
						.orElse(""));

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		String idProjetoEDocs = (ctx.getIdProcesso() != null && !ctx.getIdProcesso().isEmpty()) ? ctx.getIdProcesso()
				: ctx.getProjeto().idProcessoEdocs();

		logger.info("ID processo no Edocs para despacho : {}", idProjetoEDocs);

		DespacharProjetoDto despacharProjetoDto = new DespacharProjetoDto(idDestino, mensagem, restricaoAcessoBodyDto,
				idProjetoEDocs, idPapelResponsavel);

		return EdocsWebClient.depacharProcesso(ctx.getToken(), despacharProjetoDto);

	}

	private String despacharProcessoOrgaoOrigem(FluxoContextoIntegracaoDto ctx) {

		logger.info("Despachar processo E-Docs DIC do projeto {} para Orgao de Origem..",
				ctx.getProjeto().id());

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(ctx.getProjeto().subResponsavelProponente());

		String idDestino = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::LotacaoGuid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::LotacaoGuid)
						.orElse(""));

		String tokenLimpo = ctx.getToken().replace("Bearer ", "").trim();

		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());

		String guidPapelUsuario = listaPapeisUsuario.stream()
				.filter(papel -> papel.Prioritario())
				.findFirst()
				.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null))
				.Guid();

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		String idProjetoEDocs = (ctx.getIdProcesso() != null && !ctx.getIdProcesso().isEmpty()) ? ctx.getIdProcesso()
				: ctx.getProjeto().idProcessoEdocs();

		DespacharProjetoDto despacharProjetoDto = new DespacharProjetoDto(idDestino,
				"Despacho gerado via sistema de captação - SISCAP", restricaoAcessoBodyDto,
				idProjetoEDocs, guidPapelUsuario);

		return EdocsWebClient.depacharProcesso(ctx.getToken(), despacharProjetoDto);

	}

	private boolean validarMovimentacaoProcessoEdcos(String token, String IdProcessoEdocs) {
		logger.info(
				"Verificar se a movimentação pretendida no E-Docs pode ser feita pelo usuario que está executando a ação. Processo E-Docs {}.",
				IdProcessoEdocs);
		String guiIdLotacaoUsuario = this.recuperarLotacaoGuiUsuarioExecutandoAcao(token);
		logger.info(
				"Lotação do usuário {}.", guiIdLotacaoUsuario);
		LocalCustodiaProcessoEdocsDto localCustodia = EdocsWebClient.buscarLocalCustodiaProcessoEdocs(token,
				IdProcessoEdocs);
		return localCustodia.id().equalsIgnoreCase(guiIdLotacaoUsuario);
	}

	private String recuperarLotacaoGuiUsuarioExecutandoAcao(String token) {

		String tokenLimpo = token.replace("Bearer ", "").trim();
		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());

		String guidLotacaoUsuario = listaPapeisUsuario.stream()
				.filter(papel -> papel.Prioritario())
				.findFirst()
				.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null))
				.LotacaoGuid();

		return guidLotacaoUsuario;
	}

	private String encerrarProcessoEdcosClient(FluxoContextoIntegracaoDto ctx) {

		String desfecho = "Encerramento do processo gerado via sistema de captação - SISCAP";

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		String idProjetoEDocs = (ctx.getIdProcesso() != null && !ctx.getIdProcesso().isEmpty()) ? ctx.getIdProcesso()
				: ctx.getProjeto().idProcessoEdocs();

		String tokenLimpo = ctx.getToken().replace("Bearer ", "").trim();
		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());

		String guidPapelUsuario = listaPapeisUsuario.stream()
				.filter(papel -> papel.Prioritario())
				.findFirst()
				.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null))
				.Guid();

		EncerrarProcessoEdocsDto encerrarProcessoEdocsDto = new EncerrarProcessoEdocsDto(desfecho,
				restricaoAcessoBodyDto, idProjetoEDocs, guidPapelUsuario);

		return EdocsWebClient.encerrarProcesso(ctx.getToken(), encerrarProcessoEdocsDto);

	}

	public List<EtapasIntegracaoDto> consultarFasesIntegracaoEdocsProjeto(Long idProjeto) {
		logger.info("Consultando fases integracao projeto id {}.", idProjeto);
		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);
		return this.etapasPorChave.getOrDefault(chave, Collections.emptyList());
	}

	private List<ProcessoVinculadoDocumentoDto> consultarProcessosEdocsVinculadosDocumento(String idDocumentoEdocs,
			String token) {
		logger.info("Iniciar consulta dos processos vinculados ao documento id {}.", idDocumentoEdocs);
		return EdocsWebClient.buscarProcessosVinculadosDocumento(token, idDocumentoEdocs);
	}

	private List<ProcessoDocumentosAtoProcessoDto> consultarDocumentosAtoProcesso(String idProcessoEdocs, String idAto,
			String token) {
		logger.info("Iniciar consulta documentos de ato vinculado a um processo E-Docs id {} - Ato id {}.",
				idProcessoEdocs, idAto);
		return EdocsWebClient.buscarDocumentosAtoProcesso(token, idProcessoEdocs, idAto);
	}

	private List<AtosProcessoEdocsDto> consultarAtosProcessoEdocs(String idProcessoEdocs, String token) {
		logger.info("Iniciar consulta Atos vinculados a um processo E-Docs id {}.", idProcessoEdocs);
		return EdocsWebClient.buscarAtosProcessoEdocs(token, idProcessoEdocs);
	}

	private String desentranharDocumentoProcessoEdocs(String idProcessoEdocs, String sequencia,
			String subResponsavelProponente, String token) {

		logger.info("Iniciar processo para desentranhar documento do E-Docs.");

		String justificativa = "DESENTRANHAR DIC DO PROCESSO PARA SUBSTITUICAO.";

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(subResponsavelProponente);

		String idPapelResponsavel = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::Guid)
						.orElse(""));

		String[] sequenciais = { sequencia };

		DesentranharArquivoProcessoEdocsDto desentranharBodyDto = new DesentranharArquivoProcessoEdocsDto(justificativa,
				restricaoAcessoBodyDto, idProcessoEdocs, idPapelResponsavel, sequenciais);

		return EdocsWebClient.desentranharDocumentosProcesso(token, desentranharBodyDto);

	}

	private String entranharDocumentosProcessoEdocs(String idProcessoEdocs, String[] idDocumentosEntranhar,
			String subResponsavelProponente, String token) {

		logger.info("Iniciar processo para entranhar documento no E-Docs.");

		String justificativa = "Entranhamento DIC via Sistema de Caputação (SISCAP)";

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(subResponsavelProponente);

		String idPapelResponsavel = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::Guid)
						.orElse(""));

		EntranharDocumentosProcessoEdocsDto entranharDocumentosBodyDto = new EntranharDocumentosProcessoEdocsDto(
				justificativa, idDocumentosEntranhar, restricaoAcessoBodyDto, idProcessoEdocs, idPapelResponsavel);

		return EdocsWebClient.entranharDocumentosProcesso(token, entranharDocumentosBodyDto);

	}

	public void entranharPareceresDIC(Long idProjeto) {

		logger.info("Iniciando processo para Entranhamento de Pareceres do projeto {} no E-Docs..", idProjeto);

		Set<ProjetoParecer> pareceresProjeto = projetoParecerService.buscarPorProjeto(projetoService.buscar(idProjeto));

		pareceresProjeto.stream()
				.forEach(parecer -> {
					if (!projetoParecerService.verificarCapturaParecer(parecer.getId())) {
						throw new ValidacaoSiscapException(
								List.of("Parecer não possui id de documento do E-Docs registrado, nao deve ter sido capturado."));
					}
				});

		pareceresProjeto.stream()
				.forEach(parecer -> {
					if (projetoParecerService.verificarEntranhamentoParecer(parecer.getId())) {
						throw new ValidacaoSiscapException(
								List.of("Parecer já entranhado ao processo no E-Docs."));
					}
				});

		ProjetoDto projetoDto = projetoService.buscarPorId(idProjeto);

		var chave = new ChaveEtapasIntegracao(idProjeto, ContextoIntegracaoEdocsEnum.DIC);

		this.adicionarEtapa(
				chave,
				new EtapasIntegracaoDto(idProjeto,
						EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO, true, false,
						false));

		this.entranharDocumentosProcesso(projetoDto, pareceresProjeto)
				.subscribe(
						mensagem -> logger.info("SUCESSO: {}", mensagem),
						erro -> logger.info("ERRO: {}", erro));

		return;

	}

	private Mono<String> entranharDocumentosProcesso(ProjetoDto projetoDto, Set<ProjetoParecer> pareceresProjeto) {

		if (pareceresProjeto.stream().anyMatch(parecer -> parecer.getGuidDocumentoEdocs().isEmpty()))
			throw new ValidacaoSiscapException(
					List.of("Nenhum ID de documento informado para entranhamento ao processo no E-Docs."));

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(projetoDto, token, pareceresProjeto.stream()
						.map(ProjetoParecer::getGuidDocumentoEdocs)
						.toArray(String[]::new)))
				.flatMap(ctx -> entranharDocumentoEdocs(ctx))
				.flatMap(ctx -> consultarSituacaoEntranhamento(ctx))
				.doOnSuccess(retorno -> {
					pareceresProjeto.stream().forEach(parecer -> projetoParecerService
							.atualizarStatusParecer(parecer.getId(), StatusParecerEnum.ENTRANHADO_EDOCS));
					projetoService.enviarEmailGerenciaSubcap(projetoDto.id());
				})
				.thenReturn("Entranhamento dos pareceres referente ao DIC concluída com sucesso.");

	}

	private Mono<String> entranharParecerProcesso(ProjetoDto projetoDto, Long idParecer, String subJwt) {

		Projeto projeto = projetoService.buscar(projetoDto.id());

		ProjetoParecer projetoParecer = projetoParecerService.buscarPorProjeto(projeto).stream()
				.filter(parecer -> parecer.getGuidUnidadeOrganizacao().equals(guiddestinoSUBCAP))
				.findFirst()
				.orElse(null);

		if (projetoParecer == null)
			throw new ValidacaoSiscapException(
					List.of(String.format("Parecer SUBCAP - GEOC não encontrado para o projeto ID: %d",
							projetoDto.id())));

		var chave = new ChaveEtapasIntegracao(projetoDto.id(), ContextoIntegracaoEdocsEnum.DIC);

		return buscarTokenReativo(subJwt)
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.ENTRANHARARQUIVO,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(projetoDto, token,
						new String[] { projetoParecer.getGuidDocumentoEdocs() }))
				.flatMap(ctx -> entranharDocumentoEdocs(ctx))
				.flatMap(ctx -> consultarSituacaoEntranhamento(ctx))
				.flatMap(retorno -> Mono
						.fromRunnable(() -> projetoService.enviarEmailSubSecretariaSubcap(projetoDto.id()))
						.subscribeOn(Schedulers.boundedElastic()) // evita travar o event loop
						.thenReturn("Entranhamento do parecer referente ao DIC concluído com sucesso."));

	}

	public Mono<String> enviarArquivoAssinaturasPendentes(Long idPrograma, List<String> assinantes,
			String nomeArquivo) {

		logger.info("Iniciando processo para criar arquivo no E-Docs com pendencia de suas assinaturas..");

		Resource resourceArquivo = relatoriosService.gerarArquivo("PROGRAMA", idPrograma.intValue());

		String subJwt = autenticacaoService.getUsuarioSub();
		String tokenArmazenado = AutorizacaoACService.getEdocsToken(subJwt);
		String tokenLimpo = tokenArmazenado.replace("Bearer ", "").trim();

		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());

		String guidPapelUsuario = listaPapeisUsuario.stream()
				.filter(papel -> papel.Prioritario())
				.findFirst()
				.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null))
				.Guid();

		return this
				.capturarArquivoAssinaturaPendentesReativo(idPrograma, resourceArquivo, nomeArquivo, guidPapelUsuario,
						assinantes)
				.map(dto -> dto.getIdDocumentoAssinarFaseAssinatura())
				.flatMap(idDocumento -> Mono.just(idDocumento));

	}

	private Mono<FluxoContextoIntegracaoDto> capturarArquivoAssinaturaPendentesReativo(Long idPrograma,
			Resource arquivo,
			String nomeArquivo, String idPapelCapturador, List<String> assinantes) {

		final long tamanho;
		try {
			tamanho = arquivo.contentLength();
		} catch (IOException e) {
			return Mono.error(new RuntimeException("Falha ao obter tamanho do arquivo", e));
		}

		var chave = new ChaveEtapasIntegracao(idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(idPrograma, EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(token, assinantes))
				.flatMap(ctx -> gerarUrlUpload(ctx, tamanho))
				.flatMap(ctx -> uploadArquivo(ctx, arquivo, nomeArquivo))
				.flatMap(ctx -> enviarArquivoFaseAssinatura(ctx, arquivo, nomeArquivo, idPapelCapturador, idPrograma))
				.doOnSuccess(retorno -> {
					finalizaTodasEtapas(chave);
					logger.info("Arquivo {} capturado em fase de assinatur com sucesso", nomeArquivo);
				})
				.doOnError(e -> logger.error("Erro ao capturar arquivo {} em fase de assinatura no E-Docs. {}",
						nomeArquivo, e));

	}

	private Mono<FluxoContextoIntegracaoDto> enviarArquivoFaseAssinatura(FluxoContextoIntegracaoDto ctx,
			Resource arquivo, String nomeArquivo, String idPapelCapturador, Long idPrograma) {

		logger.info("Iniciar processo de enviar arquivo para o E-Docs em fase de assinatura.");

		var chave = new ChaveEtapasIntegracao(idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA);

		return FeignReativo
				.fromFeign(() -> enviarDocumentoFaseAssinaturaEdocs(ctx.getToken(),
						idPapelCapturador,
						ctx.getAssinantes(), nomeArquivo,
						ctx.getDtoUploadArquivoResponse().identificadorTemporarioArquivoNaNuvem()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao consultar situacao do evento de envio do arquivo fase de assinatura.")))
				.doOnSuccess(retorno -> {
					logger.info("Retorno envio documento fase assinatura : {}", retorno);
					ctx.setIdDocumentoAssinarFaseAssinatura(retorno.replace("\"", ""));
				})
				.doOnError(e -> {
					logger.error(
							"Falha ao executar chamada ao endpoint para envio do arquivo fase de assinatura via E-Docs.",
							e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINAPENDENTE);
				})
				.thenReturn(ctx);

	}

	private String enviarDocumentoFaseAssinaturaEdocs(String token, String idPapelCapturadorAssinante,
			List<String> assinantes, String nomeArquivo, String identificadorTemporarioArquivoNaNuvem) {

		logger.info("Iniciar processo para entranhar documento no E-Docs.");

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		EnviaArquivoFaseAssinaturaBodyDto enviaArquivoFaseAssinaturaBodyDto = new EnviaArquivoFaseAssinaturaBodyDto(
				idPapelCapturadorAssinante,
				classeDocumentoId,
				assinantes.toArray(new String[0]),
				nomeArquivo,
				true,
				restricaoAcessoBodyDto,
				identificadorTemporarioArquivoNaNuvem);

		return EdocsWebClient.enviarDocumentoFaseAssinatura(token, enviaArquivoFaseAssinaturaBodyDto);

	}

	private DadosDocumentoDto consultarDadosArquivoCapturado(String idDocumento, String token) {
		logger.info("Iniciar consulta dados arquivo capturado id {}.", idDocumento);
		return EdocsWebClient.buscarDadosArquivo(token, idDocumento);
	}

	public Mono<String> assinarArquivoPendenteReativo(Long idPrograma,
			String idDocumentoAssinarFaseAssinatura) {

		var chave = new ChaveEtapasIntegracao(idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(idPrograma, EtapasIntegracaoEdocsEnum.CAPTURAASSINAPENDENTE, true, false,
						false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINAPENDENTE,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(token, idDocumentoAssinarFaseAssinatura, ""))
				.flatMap(ctx -> assinarArquivoFaseAssinatura(ctx))
				.filter(ctx -> !ctx.getIdEventoAssinatura().isBlank()) // o id do evento de captura só existe após todos
																		// assinarem;
				.flatMap(ctx -> consultarSituacaoEventoAssinatura(ctx))
				.flatMap(ctx -> {
					var situacao = ctx.getSituacaoEventoAto();
					if (situacao == null || situacao.idDocumento() == null) {
						return Mono.empty(); // ausência válida
					}
					return Mono.just(situacao.idDocumento());
				})
				.doOnSuccess(retorno -> finalizaTodasEtapas(chave))
				.doOnError(e -> logger.error("Erro ao assinar arquivo do programa id {}", idPrograma, e));

	}

	private Mono<FluxoContextoIntegracaoDto> assinarArquivoFaseAssinatura(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar processo para assinar arquivo com pendencia de assinatura para o E-Docs.");

		var chave = new ChaveEtapasIntegracao(ctx.getIdPrograma(), ContextoIntegracaoEdocsEnum.PROGRAMA);

		return FeignReativo
				.fromFeign(() -> assinaArquivo(ctx.getToken(), ctx.getIdDocumentoAssinarFaseAssinatura()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.doOnSuccess(retorno -> {
					if (retorno.capturado()) {
						ctx.setIdEventoAssinatura(retorno.idCapturaEvento());
						finalizaTodasEtapas(chave);
					} else {
						ctx.setIdEventoAssinatura("");
					}
				})
				.doOnError(e -> {
					logger.error(
							"Falha ao executar chamada ao endpoint para assinar arquivo fase de assinatura via E-Docs.",
							e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINAPENDENTE);
				})
				.thenReturn(ctx);

	}

	private RetornoAssinaturaEdocsDto assinaArquivo(String token, String idDocumentoFaseAssinatura) {
		logger.info("Assinando documento id {} no E-Docs.", idDocumentoFaseAssinatura);
		return EdocsWebClient.assinarDocumentoEDocsFaseAssinatura(token, idDocumentoFaseAssinatura);
	}

	public Mono<FluxoContextoIntegracaoDto> autuarProgramaProjetoReativo(Long idPrograma, String idDocumentoEdocs,
			ProgramaDto programaDto) {

		String[] documentoEntranhar = { idDocumentoEdocs };

		var chave = new ChaveEtapasIntegracao(idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA);

		this.adicionarEtapa(chave,
				new EtapasIntegracaoDto(idPrograma, EtapasIntegracaoEdocsEnum.AUTUAR, false, false, false));

		return buscarTokenReativo()
				.doOnError(erro -> {
					String erroBuscarToken = "Token inválido : Sua permissão de acesso ao E-Docs expirou, gentileza realizar um novo acesso ao SISCAP.";
					logger.error("Erro ao buscar Token", erro.getMessage());
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.CAPTURAASSINA,
							erroBuscarToken);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.map(token -> new FluxoContextoIntegracaoDto(token, idPrograma, documentoEntranhar, programaDto))
				.flatMap(ctx -> autuarProcessoMonoPrograma(ctx))
				.flatMap(ctx -> consultarSituacaoEventoAtuacaoPrograma(ctx))
				.flatMap(ctx -> consultarDadosAutuacaoEdocs(ctx))
				.doOnSuccess(retorno -> this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO,
						true, true));

	}

	private Mono<FluxoContextoIntegracaoDto> consultarDadosAutuacaoEdocs(FluxoContextoIntegracaoDto ctx) {
		return FeignReativo.fromFeign(() -> consultarDadosProcessoEdocs(
				ctx.getIdProcesso(),
				ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao executar chamada ao endpoint para consultar dados do processo via E-Docs.")))
				.doOnSuccess(retornoDadosProcesso -> ctx.setProtocolo(retornoDadosProcesso.protocolo()))
				.thenReturn(ctx);
	}

	private String autuarProcessoPrograma(ProgramaDto programaDTO, String token, String idDocumentoCapturado) {

		logger.info("Iniciar autuacao do processo para o programa id {} - documento id {}.", programaDTO.id(),
				idDocumentoCapturado);

		String idClasse = classeDocumentoId;

		String tokenLimpo = token.replace("Bearer ", "").trim();
		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService
				.listarPapeisAgentePublicoPorSub(userInfo.subNovo());

		String idPapelResponsavel = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::Guid)
						.orElse(""));

		String idLocal = papeisAgentePublico.stream()
				.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
				.findFirst()
				.map(ACAgentePublicoPapelDto::LotacaoGuid)
				.orElseGet(() -> papeisAgentePublico.stream()
						.findFirst()
						.map(ACAgentePublicoPapelDto::LotacaoGuid)
						.orElse(""));

		String resumo = String.format("AUTUAÇÃO PROGRAMA - %s", programaDTO.titulo());

		List<String> idsAgentesInteressados = programaDTO.equipeCaptacao()
				.stream()
				.map(membro -> membro.subPessoa())
				.collect(Collectors.toList());

		List<String> idsDocumentosEntranhados = List.of(idDocumentoCapturado);

		AutuarProjetoDto autuarProjetoDto = new AutuarProjetoDto(idClasse, idPapelResponsavel, idLocal, resumo,
				idsAgentesInteressados, idsDocumentosEntranhados);

		return EdocsWebClient.autuarProcesso(token, autuarProjetoDto);

	}

	private Mono<FluxoContextoIntegracaoDto> autuarProcessoMonoPrograma(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciando autuacao do processo referente ao Programa id {} no E-Docs.", ctx.getIdPrograma());

		var chave = new ChaveEtapasIntegracao(ctx.getIdPrograma(), ContextoIntegracaoEdocsEnum.PROGRAMA);

		return FeignReativo.fromFeign(() -> autuarProcessoPrograma(
				ctx.getProgramaDto(),
				ctx.getToken(),
				ctx.getIdDocumentos()[0]))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Falha ao executar chamada ao endpoint para autuar um processo via E-Docs.")))
				.doOnSuccess(IdEventoComandoAutuacao -> {
					logger.info("Autuacao foi comandada no E-Docs - ID {}", IdEventoComandoAutuacao);
					ctx.setIdEventoAutuar(IdEventoComandoAutuacao.replace("\"", ""));
				})
				.doOnError(e -> {
					logger.error("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs. {}", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR);
				})
				.thenReturn(ctx);

	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoEventoAssinatura(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento assinatura documento - id {}.", ctx.getIdEventoAssinatura());

		var chave = new ChaveEtapasIntegracao(ctx.getIdPrograma(), ContextoIntegracaoEdocsEnum.PROGRAMA);

		return FeignReativo
				.fromFeign(() -> EdocsWebClient.buscarSituacaoEvento(ctx.getToken(), ctx.getIdEventoAssinatura()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono
						.error(new RuntimeException("Falha ao consultar situacao evento de ASSINATURA via E-Docs.")))
				.doOnSuccess(resultConsultaEvento -> ctx.setSituacaoEventoAto(resultConsultaEvento))
				.doOnError(e -> {
					logger.error("Falha ao consultar situacao evento de ASSINATURA de um documento via E-Docs.", e);
					registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.ASSINADO);
				})
				.thenReturn(ctx);
	}

	private Mono<FluxoContextoIntegracaoDto> consultarSituacaoEventoAtuacaoPrograma(FluxoContextoIntegracaoDto ctx) {

		logger.info("Iniciar consulta situacao evento AUTUACAO id {}.", ctx.getIdEventoAutuar());

		var chave = new ChaveEtapasIntegracao(ctx.getIdPrograma(), ContextoIntegracaoEdocsEnum.PROGRAMA);

		return FeignReativo.fromFeign(() -> consultarSituacaoEventoEdocs(ctx.getIdEventoAutuar(), ctx.getToken()))
				.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
				.filter(dto -> {
					boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
					if (!isConcluido) {
						logger.warn("Status não concluído: {}", dto.situacao());
					}
					return isConcluido;
				})
				.repeatWhenEmpty(flux -> flux.delayElements(Duration.ofSeconds(2)))
				.timeout(Duration.ofMinutes(1))
				.switchIfEmpty(Mono
						.error(new RuntimeException("Falha ao consultar situacao do evento de AUTUACAO DO PROCESSO ID "
								+ ctx.getIdEventoAutuar() + ".")))
				.doOnRequest(
						n -> this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR, true, true))
				.doOnError(e -> {
					logger.error("Falha ao verificar situacao do evento de autuacao do processo no E-Docs.", e);
					this.registrarFalhaEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR);
				})
				.doOnSuccess(resultConsultaEvento -> {
					ctx.setIdProcesso(resultConsultaEvento.idProcesso());
					this.atualizarEtapa(chave, EtapasIntegracaoEdocsEnum.AUTUAR, true, true);
				})
				.thenReturn(ctx);

	}

	public List<EtapasIntegracaoDto> consultarFasesIntegracaoEdocsPrograma(Long idPrograma) {
		logger.info("Consultando fases integracao programa id {}.", idPrograma);
		var chave = new ChaveEtapasIntegracao(idPrograma, ContextoIntegracaoEdocsEnum.PROGRAMA);
		return this.etapasPorChave.getOrDefault(chave, Collections.emptyList());
	}

}