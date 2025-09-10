package br.gov.es.siscap.service;

import br.gov.es.siscap.client.EdocsWebClient;
import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.ACUserInfoDto;
import br.gov.es.siscap.dto.edocswebapi.*;
import br.gov.es.siscap.enums.edocs.EtapasIntegracaoEdocsEnum;
import br.gov.es.siscap.enums.edocs.SituacaoEventoEdocsEnum;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	private final Logger logger = LogManager.getLogger(IntegraccaoEdocsService.class);

	private Map<Long, List<EtapasIntegracaoDto>> etapasPorProjeto = new HashMap<>();

	public void atualizarEtapa(Long idProjeto, EtapasIntegracaoEdocsEnum etapaEnum, boolean iniciou, boolean finalizou ) {
		List<EtapasIntegracaoDto> etapas = etapasPorProjeto.get(idProjeto);
		if (etapas != null) {
			for (EtapasIntegracaoDto etapa : etapas) {
				if (etapa.getEtapa().equals(etapaEnum)) {
					etapa.setIniciou(iniciou);
					etapa.setFinalizou(finalizou);
					etapa.setErro(false);
					break;
				}
			}
		}
	}

	public void registrarFalhaEtapa(Long idProjeto, EtapasIntegracaoEdocsEnum etapaEnum ) {
		List<EtapasIntegracaoDto> etapas = etapasPorProjeto.get(idProjeto);
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
		logger.info("FEZ O REGISTRO DA FALHA : {}", etapas );
	}

	// public List<EtapasIntegracaoDto> consultarEtapas(Long idProjeto) {
	// 	List<EtapasIntegracaoDto> etapas = etapasPorProjeto.getOrDefault(idProjeto, new ArrayList<>());
	// 	try {
	// 		logger.info("Payload Etapas: {}", new ObjectMapper().writeValueAsString(etapas));
	// 	} catch (JsonProcessingException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// 	return etapasPorProjeto.getOrDefault(idProjeto, new ArrayList<>());
	// }

	public void limparEtapas(Long idProjeto) {
		etapasPorProjeto.remove(idProjeto);
	}

	public void adicionarEtapa(Long idProjeto, EtapasIntegracaoDto etapa) {
		etapasPorProjeto
			.computeIfAbsent( idProjeto, k -> new ArrayList<>() ) // cria lista se não existir
			.add(etapa);
	}
	
	public void assinarAutuarDespacharDicProccessoSUBCAP( Resource arquivoDic, String nomeArquivo, Long idProjeto ){

		logger.info("Iniciando processo para Autuacao/Despacho do projeto {} para SUBCAP..", idProjeto);

		this.limparEtapas(idProjeto);

		ProjetoDto projetoDtoIntegrando = projetoService.buscarPorId(idProjeto);
		
		autuarDicProjetoReativo(projetoDtoIntegrando, arquivoDic, nomeArquivo)
			.subscribe(
				mensagem -> logger.info("SUCESSO: {}", mensagem),
				erro -> logger.info("ERRO: {}", erro)
			);

		return; 
		
	}

	public Mono<String> responderComplementacaoDicProjeto(ProjetoDto projetoDto, Resource arquivoCorrigido, String nomeArquivo) {

		// tamanho do arquivo para captura..
		final Long tamanho;
		try {
			tamanho = arquivoCorrigido.contentLength();
		} catch (IOException e) {
			throw new RuntimeException(" Falha ao obter tamanho do arquivo", e);
		}
		
		return buscarTokenReativo()
			.switchIfEmpty( Mono.error(new RuntimeException( "Token não encontrado ao buscarTokenReativo()" ) ) )
			.flatMap( token ->
				FeignReativo.fromFeign( () -> avocarProcessoEDocs(projetoDto, token) )
					.retryWhen( Retry.fixedDelay(3, Duration.ofSeconds(2) ) )
					.switchIfEmpty( Mono.error(new RuntimeException("Falha ao avocar processo via E-Docs.")))
					.doOnSuccess( retornoAvocar -> logger.info("Avocação realizada: {}", retornoAvocar ) )
					.doOnError( e -> this.registrarFalhaEtapa(projetoDto.id(),EtapasIntegracaoEdocsEnum.AVOCAR) )
				.flatMap( idEventoAvocar -> 
					FeignReativo.fromFeign( () ->  consultarSituacaoEventoEdocs( idEventoAvocar.replace("\"", ""), token ) )
					.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
					.filter( dto -> { return SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao()); } )
					.timeout( Duration.ofMinutes(1)) // Timeout total em minutos
					.switchIfEmpty( Mono.error(new RuntimeException("Falha ao consultar situacao do evento de CAPTURA ID " + idEventoAvocar + ".")))
					.doOnSuccess( situacaoEventoDto -> {
						this.atualizarEtapa( projetoDto.id(), EtapasIntegracaoEdocsEnum.AVOCAR, true, true );
					} )
					.doOnError( e -> { 	logger.error("Falha ao verificar situacao do evento de avocar o processo.", e );
										this.registrarFalhaEtapa(projetoDto.id(),EtapasIntegracaoEdocsEnum.AVOCAR ); } )
				)
					.flatMap( dtoSituacaoEvento -> 
						FeignReativo.fromFeign( () -> EdocsWebClient.gerarUrlUploadArquivo(token, tamanho) )
							.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
							.doOnRequest(n -> { this.adicionarEtapa( projetoDto.id(), new EtapasIntegracaoDto( projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false ) ); } )
							.switchIfEmpty(Mono.error(new RuntimeException("Falha na geração da URL temporária para Upload do Arquivo.")))
							.doOnSuccess( urlDto -> { logger.info("URL gerada: {}", urlDto.url()); } )
							.doOnError( e -> { logger.error("Falha ao gerar URL", e );
											this.registrarFalhaEtapa( projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA );
										} )
						.flatMap( urlDto ->
							FeignReativo.fromFeign( () ->  UploadS3Service.enviarArquivoParaS3OkHttp(
									urlDto.url(), 
									urlDto.body(), 
									arquivoCorrigido, 
									nomeArquivo, 
									token) )
								.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
								.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.")))
								.doOnSuccess( retornoEnvio -> logger.info("Upload feito com sucesso: {}", retornoEnvio ))
								.doOnError( e -> { 	logger.error("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.", e ); 
													this.registrarFalhaEtapa(projetoDto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
													} )
							.flatMap( retornoUpload -> 
									FeignReativo.fromFeign( () ->  
											capturarAssinarDocumento(urlDto.identificadorTemporarioArquivoNaNuvem(),
											nomeArquivo,
											token
										))
										.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
										.switchIfEmpty(Mono.error(new RuntimeException("Falha ao capturar/assinar documento via E-Docs.")))
										.doOnSuccess( retornoCaptura -> logger.info("Captura realizada: {}", retornoCaptura ))
										.doOnError( e -> { logger.error("Falha ao enviar caputura do arquivo para o servidor S3 do E-Docs.", e );
													this.registrarFalhaEtapa(projetoDto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA); } )
									.flatMap( idEventoCaptura -> 
											FeignReativo.fromFeign( () ->
												consultarSituacaoEventoEdocs( idEventoCaptura.replace("\"", ""), token ) 
												)
												.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
												.filter(dto -> {
													boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
													if (!isConcluido) {
														logger.warn("Status não concluído: {}", dto.situacao()); // Log de status inesperado
													}
													return isConcluido;
												})
												.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
												.timeout( Duration.ofMinutes(1) ) // Timeout total em minutos
												.switchIfEmpty( Mono.error(new RuntimeException("Falha ao consultar situacao do evento de CAPTURA ID " + idEventoCaptura + ".")))
												.doOnSuccess( situacaoEventoDto -> {
														logger.info("Captura confirmada: {}", situacaoEventoDto.situacao() );
														this.atualizarEtapa(projetoDto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, true );
													} )
												.doOnError( e -> { logger.error("Falha ao verificar situacao do evento de caputura do arquivo para o servidor S3 do E-Docs.", e );
																	this.registrarFalhaEtapa(projetoDto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA); } )
											.flatMap( dtoSituacaoEventoCaptura -> 
												FeignReativo.fromFeign( () ->  
														consultarProcessosEdocsVinculadosDocumento( dtoSituacaoEventoCaptura.idDocumento(), token )
													)
													.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
													.timeout( Duration.ofMinutes(1) ) // Timeout total em minutos
													.switchIfEmpty( Mono.error(new RuntimeException("Falha ao consultar processos vinculados ao documento.")))
													.doOnError( e -> { logger.error("Falha ao consulta lista de processos vinculados ao documento.", e ); } )
												.flatMap( listaProcessosVinculados -> 
													Mono.justOrEmpty(
														listaProcessosVinculados.stream()
															.filter(processo -> processo.protocolo().equals(projetoDto.protocoloEdocs()))
															.findFirst()
													)

												)
												.flatMap( processoVinculado -> 
													FeignReativo.fromFeign( () ->  
														consultarProcessosEdocsVinculadosDocumento( dtoSituacaoEventoCaptura.idDocumento(), token )
													)
												)
											)
												
								)
							)
						)
					)
			).thenReturn("Atualizacao de DIC atendendo aos complementos concluido com sucesso.");

	}

	public Mono<String> autuarDicProjetoReativo(ProjetoDto projeto, Resource arquivo, String nomeArquivo) {
		
		return FeignReativo.fromFeign( () -> {
				try {
					return arquivo.contentLength();
				} catch (IOException e) {
					throw new RuntimeException(" Falha ao obter tamanho do arquivo", e);
				} 
			})
			.flatMap( tamanho -> 
				buscarTokenReativo()
				.switchIfEmpty(Mono.error(new RuntimeException("Token não encontrado ao buscarTokenReativo()")))
				.flatMap( token -> 
					FeignReativo.fromFeign( () -> EdocsWebClient.gerarUrlUploadArquivo(token, tamanho) )
						.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
						.doOnRequest(n -> { this.adicionarEtapa( projeto.id(), new EtapasIntegracaoDto( projeto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, false, false ) ); } )
						.switchIfEmpty(Mono.error(new RuntimeException("Falha na geração da URL temporária para Upload do Arquivo.")))
						.doOnSuccess( urlDto -> { logger.info("URL gerada: {}", urlDto.url()); } )
						.doOnError( e -> { logger.error("Falha ao gerar URL", e );
										   this.registrarFalhaEtapa(projeto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
		 								 } )
					.flatMap( urlDto ->
						FeignReativo.fromFeign( () ->  UploadS3Service.enviarArquivoParaS3OkHttp(
								urlDto.url(), 
								urlDto.body(), 
								arquivo, 
								nomeArquivo, 
								token) )
							.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
							.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.")))
							.doOnSuccess( retornoEnvio -> logger.info("Upload feito com sucesso: {}", retornoEnvio ))
							.doOnError( e -> { 	logger.error("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.", e ); 
												this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA);
												} )
						.flatMap( retornoUpload -> 
							FeignReativo.fromFeign( () ->  
								capturarAssinarDocumento(urlDto.identificadorTemporarioArquivoNaNuvem(),
									nomeArquivo,
									token
								))
								.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
								.switchIfEmpty(Mono.error(new RuntimeException("Falha ao capturar/assinar documento via E-Docs.")))
								.doOnSuccess( retornoCaptura -> logger.info("Captura realizada: {}", retornoCaptura ))
								.doOnError( e -> { logger.error("Falha ao enviar caputura do arquivo para o servidor S3 do E-Docs.", e );
													this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA); } )
							.flatMap( idEventoCaptura -> 
								FeignReativo.fromFeign( () ->  
									consultarSituacaoEventoEdocs( 
										idEventoCaptura.replace("\"", ""), 
										token  
									))
									.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
									.filter(dto -> {
										boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
										if (!isConcluido) {
											logger.warn("Status não concluído: {}", dto.situacao()); // Log de status inesperado
										}
										return isConcluido;
									})
									.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
									.timeout( Duration.ofMinutes(1)) // Timeout total em minutos
									.switchIfEmpty( Mono.error(new RuntimeException("Falha ao consultar situacao do evento de CAPTURA ID " + idEventoCaptura + ".")))
									.doOnSuccess( situacaoEventoDto -> {
											logger.info("Captura confirmada: {}", situacaoEventoDto.situacao() );
											this.atualizarEtapa(projeto.id(), EtapasIntegracaoEdocsEnum.CAPTURAASSINA, true, true );
										}
									)
									.doOnError( e -> { logger.error("Falha ao verificar situacao do evento de caputura do arquivo para o servidor S3 do E-Docs.", e );
														this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.CAPTURAASSINA); } )
								.flatMap( dtoSituacaoEvento -> 
									FeignReativo.fromFeign( () ->  
										autuarProcesso( 
											projeto,
											token,
											dtoSituacaoEvento.idDocumento() 
										))
										.doOnRequest(n -> {
											this.adicionarEtapa( projeto.id(), new EtapasIntegracaoDto( projeto.id(), EtapasIntegracaoEdocsEnum.AUTUAR, true, false, false) );											
											})
										.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
										.switchIfEmpty( Mono.error(new RuntimeException("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs.")))
										.doOnError( e -> { logger.error("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs. {}", e );
															this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.AUTUAR); } )
									.flatMap( idEventoAutuar -> 
										FeignReativo.fromFeign( () ->  
											consultarSituacaoEventoEdocs( 
												idEventoAutuar.replace("\"", ""), 
												token  
											))
											.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
											.doOnNext(dto -> logger.info("Id do processo autuado : {}", dto.idProcesso() )) 
											.filter(dto -> {
												boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
												if (!isConcluido) {
													logger.warn("Status não concluído: {}", dto.situacao()); // Log de status inesperado
												}
												return isConcluido;
											})
											.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
											.timeout(Duration.ofMinutes(1)) // Timeout total em minutos
											.switchIfEmpty(Mono.error(new RuntimeException("Falha ao consultar situacao do evento de AUTUACAO DO PROCESSO ID " + idEventoAutuar + ".")))
											.doOnRequest( n -> {
													this.atualizarEtapa(projeto.id(), EtapasIntegracaoEdocsEnum.AUTUAR, true, true );
												})
											.doOnError( e -> { logger.error("Falha ao verificar situacao do evento de autuacao do processo no E-Docs.", e );
																this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.AUTUAR); } )
										.flatMap( dtoSituacaoEventoAutuacao -> 
											FeignReativo.fromFeign( () ->  
												despacharProcessoSUBCAP( projeto, token, dtoSituacaoEventoAutuacao.idProcesso() )
											)
											.doOnRequest(n -> {
												this.adicionarEtapa( projeto.id(), new EtapasIntegracaoDto( projeto.id(), EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, false, false) );
												})
											.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
											.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.")))
											.doOnSuccess( retorno -> logger.info("Chamada ao endpoint realizada: {}", retorno ) )
											.doOnError( e -> {  logger.error("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs. {}", e );
																this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
														 } )
											.flatMap( idEventoDespacho -> 
												FeignReativo.fromFeign( () ->  
													consultarSituacaoEventoEdocs( 
														idEventoDespacho.replace("\"", ""), 
														token  
													))
													.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
													.filter(dto -> {
														boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
														if (!isConcluido) {
															logger.warn("Status não concluído: {}", dto.situacao()); // Log de status inesperado
														}
														return isConcluido;
													})
													.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
													.timeout(Duration.ofMinutes(1)) // Timeout total em minutos
													.switchIfEmpty(Mono.error(new RuntimeException("Falha ao consultar situacao do evento de DESPACHO DO PROCESSO ID " + idEventoDespacho + ".")))
													.doOnSuccess( retorno -> {
														this.atualizarEtapa(projeto.id(), EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO, true, true );
													})
													.doOnError( e -> { logger.error("Falha ao verificar situacao do evento de despacho do processo no E-Docs.", e );
																	this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);
															 } )
												)
												.flatMap( dtoSituacaoEventoDespacho ->
													FeignReativo.fromFeign( () -> 
														consultarDadosProcessoEdocs( dtoSituacaoEventoAutuacao.idProcesso(), 
														token ) 
													)
													.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
													.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.")))
													.doOnSuccess( retornoDadosProcesso -> { 
															logger.info("Gravando Protocolo do processo E-Docs {} no processo do SISCAP.", retornoDadosProcesso.protocolo() ) ;
															projetoService.atualizarProtocoloProcessoEdocsProjeto(projeto.id(), retornoDadosProcesso.protocolo() );
															projetoService.atualizarIdArquivoCapturadoProcessoEdocsProjeto(projeto.id(),dtoSituacaoEvento.idDocumento());
														}
													)
													.doOnError( e -> { logger.error("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs. {}", e );
																		this.registrarFalhaEtapa(projeto.id(),EtapasIntegracaoEdocsEnum.DESPACHARPROCESSO);						
																	} )
												)
										)
									)
								)
							)
						)
					)
				)
			).thenReturn("Autuação do DIC concluido com sucesso");

	}
	
	private Mono<String> buscarTokenReativo() {
		return Mono.just(AutorizacaoACService.getEdocsToken("accessTokenAC")); 
	}

	private SituacaoEventoDto consultarSituacaoEventoEdocs( String idEventoEdocs, String token ){
		logger.info("Iniciar consulta situacao evento id {}.", idEventoEdocs);
		return EdocsWebClient.buscarSituacaoEvento( token, idEventoEdocs );
	}

	private ProcessoEdocsDto consultarDadosProcessoEdocs( String idProcessoEdocs, String token ){
		logger.info("Iniciar consulta dados do processo E-Docs id {}.", idProcessoEdocs);
		return EdocsWebClient.buscarDadosProcessoEdocs( token, idProcessoEdocs );
	}

	private String capturarAssinarDocumento( String identificadorTemporarioArquivo, String nomeArquivo, String token  ){
		
		String tokenLimpo = token.replace("Bearer ", "").trim();
		
		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService.listarPapeisAgentePublicoPorSub(userInfo.subNovo());
		String guidPapelUsuario = listaPapeisUsuario.stream()
									.filter(papel -> papel.Prioritario() )  
									.findFirst()                            
									.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null) )
									.Guid() ; 

		String idPapelCapturadorAssinante = guidPapelUsuario ;
		
		String idClasse = this.classeDocumentoId;

		boolean credenciarCapturador = true;

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);
		CapturaAssinaturaBodyDto capturaAssinaturaBodyDto = new CapturaAssinaturaBodyDto( idPapelCapturadorAssinante, idClasse, 
																						  nomeArquivo, credenciarCapturador, 
																						  restricaoAcessoBodyDto, 
																						  identificadorTemporarioArquivo);

		return EdocsWebClient.capturarDocumento( token, capturaAssinaturaBodyDto);

	}

	private String autuarProcesso( ProjetoDto projetoDTO, String token, String idDocumentoCapturado ){
		
		logger.info("Iniciar autuacao do processo para o projeto id {} - documento id {}.", projetoDTO.id(), idDocumentoCapturado );

		String idClasse = classeDocumentoId ; 

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService.listarPapeisAgentePublicoPorSub( projetoDTO.subResponsavelProponente() );
		
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

		String resumo = String.format( "AUTUAÇÃO PROJETO - %s", projetoDTO.titulo() );
		
		List<String> idsAgentesInteressados = projetoDTO.equipeElaboracao()
			.stream()
			.map(membro -> membro.subPessoa() )
			.collect(Collectors.toList()) ;

		Optional.ofNullable(projetoDTO.subResponsavelProponente())
			.filter(v -> !idsAgentesInteressados.contains(v))
			.ifPresent(idsAgentesInteressados::add);

		Optional.ofNullable(projetoDTO.subProponente())
		
			.filter(v -> !idsAgentesInteressados.contains(v))
			.ifPresent(idsAgentesInteressados::add);

		List<String> idsDocumentosEntranhados = List.of( idDocumentoCapturado );

		AutuarProjetoDto autuarProjetoDto = new AutuarProjetoDto( idClasse, idPapelResponsavel, idLocal, resumo, idsAgentesInteressados, idsDocumentosEntranhados );

		return EdocsWebClient.autuarProcesso( token, autuarProjetoDto );

	}

	private String despacharProcessoSUBCAP( ProjetoDto projetoDTO, String token, String idProcessoEDocs ){
		
		logger.info("Iniciar depacho do processo para o projeto id {} para SUBCAP.", projetoDTO.id() );
		
		String idDestino = guiddestinoSUBCAP; 

		String mensagem = "DEPACHO AUTOMÁTICO GERADO PELO SISCAP";

		List<ACAgentePublicoPapelDto> papeisAgentePublico = AcessoCidadaoService.listarPapeisAgentePublicoPorSub( projetoDTO.subResponsavelProponente() );

		String idPapelResponsavel = papeisAgentePublico.stream()
			.filter(agente -> Boolean.TRUE.equals(agente.Prioritario()))
			.findFirst()
			.map(ACAgentePublicoPapelDto::Guid)
			.orElseGet(() -> papeisAgentePublico.stream()
				.findFirst()
				.map(ACAgentePublicoPapelDto::Guid)
				.orElse(""));

		logger.info( "Papel Responsavel Despacho : {}", idPapelResponsavel );			

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);
		DespacharProjetoDto despacharProjetoDto = new DespacharProjetoDto( idDestino, mensagem, restricaoAcessoBodyDto, idProcessoEDocs, idPapelResponsavel );

		return EdocsWebClient.depacharProcesso( token, despacharProjetoDto );

	}

	public List<EtapasIntegracaoDto> consultarFasesIntegracaoEdocsProjeto(Long idProjeto){
		return this.etapasPorProjeto.get(idProjeto);
	}

	private String avocarProcessoEDocs( ProjetoDto projetoDto, String token ){
		
		String tokenLimpo = token.replace("Bearer ", "").trim();
		
		ACUserInfoDto userInfo = AcessoCidadaoService.buscarInformacoesUsuario(tokenLimpo);

		List<ACAgentePublicoPapelDto> listaPapeisUsuario = AcessoCidadaoService.listarPapeisAgentePublicoPorSub(userInfo.subNovo());
		
		String guidPapelUsuario = listaPapeisUsuario.stream()
									.filter(papel -> papel.Prioritario() )  
									.findFirst()                            
									.orElseGet(() -> listaPapeisUsuario.stream().findFirst().orElse(null) )
									.Guid() ; 

		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);
				
		AvocarProcessoEdocsDto avocarProcessoBodyDto = new AvocarProcessoEdocsDto( 
												"Avocar o processo para reenviar o DIC com os complementos solicitados pela SUBCAP.", 
															restricaoAcessoBodyDto, 
															projetoDto.protocoloEdocs(), 
															guidPapelUsuario );

		return EdocsWebClient.avocarProcesso( token, avocarProcessoBodyDto );

	}

	private List<ProcessoVinculadoDocumentoDto> consultarProcessosEdocsVinculadosDocumento( String idDocumentoEdocs, String token ){
		logger.info("Iniciar consulta dos processos vinculados ao documento id {}.", idDocumentoEdocs);
		return EdocsWebClient.buscarProcessosVinculadosDocumento( token, idDocumentoEdocs );
	}

	// buscarAtosProcessosVinculadosDocumento( @RequestHeader("Authorization") String bearerToken, 
	// 	@PathVariable String idProcesso, @PathVariable String idAto );

	private List<ProcessoVinculadoDocumentoDto> consultarDocumentosAtoProcesso( String idProcesso, String idAto, String token ){
		logger.info("Iniciar consulta documentos de ato vinculado a um processo id {}.", idProcesso);
		return EdocsWebClient.buscarDocumentosAtoProcesso( token, idProcesso, idAto );
	}		


}