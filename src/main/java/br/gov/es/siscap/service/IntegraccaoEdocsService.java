package br.gov.es.siscap.service;

import br.gov.es.siscap.client.EdocsWebClient;
import br.gov.es.siscap.dto.edocswebapi.*;
import br.gov.es.siscap.enums.edocs.SituacaoEventoEdocsEnum;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntegraccaoEdocsService {

	@Value("")
	private String GUID_GOVES;

	private final EdocsWebClient EdocsWebClient;
	private final AcessoCidadaoAutorizacaoService AutorizacaoACService;
	private final UploadS3Service UploadS3Service;
	private final Logger logger = LogManager.getLogger(IntegraccaoEdocsService.class);

	public void assinarAutuarDespacharDicProccessoSUBCAP( Resource arquivoDic, String nomeArquivo, Integer idProjeto ){

		logger.info("Iniciando processo para Autuacao/Despacho do projeto {} para SUBCAP..", idProjeto);
		
		uploadArquivoReativo(idProjeto, arquivoDic, nomeArquivo)
			.subscribe(
				mensagem -> logger.info("SUCESSO: {}", mensagem),
				erro -> logger.info("ERRO: {}", erro)
			);

		return; 
		
	}

	public Mono<String> uploadArquivoReativo(Integer idProjeto, Resource arquivo, String nomeArquivo) {
		
		return FeignReativo.fromFeign(() -> { 
				try {
					return arquivo.contentLength();
				} catch (IOException e) {
					throw new RuntimeException(" Falha ao obter tamanho do arquivo", e);
				} 
			})
			.flatMap(tamanho -> 
				buscarTokenReativo()
				.flatMap( token -> 
					FeignReativo.fromFeign( () -> EdocsWebClient.gerarUrlUploadArquivo(token, tamanho) )
						.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
						.switchIfEmpty(Mono.error(new RuntimeException("Falha na geração da URL temporária para Upload do Arquivo.")))
						.doOnSuccess(urlDto -> logger.info("URL gerada: {}", urlDto.url()))
						.doOnError(e -> logger.error("Falha ao gerar URL", e))
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
							.doOnError( e -> logger.error("Falha ao executar UPLOAD do arquivo para o servidor S3 do E-Docs.", e ) )
						.flatMap( retornoUpload -> 
							FeignReativo.fromFeign( () ->  
								capturarAssinarDocumento(
									urlDto.identificadorTemporarioArquivoNaNuvem(),
									nomeArquivo,
									token
								))
								.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
								.switchIfEmpty(Mono.error(new RuntimeException("Falha ao capturar/assinar documento via E-Docs.")))
								.doOnSuccess( retornoCaptura -> logger.info("Captura realizada: {}", retornoCaptura ))
								.doOnError( e -> logger.error("Falha ao enviar caputura do arquivo para o servidor S3 do E-Docs.", e ) )
							.flatMap( idEventoCaptura -> 
								FeignReativo.fromFeign( () ->  
									consultarSituacaoEventoEdocs( 
										idEventoCaptura.replace("\"", ""), 
										token  
									))
									.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
									.doOnNext(dto -> logger.info("Status recebido: {}", dto.situacao())) // LOG PARA DEBUG
									.filter(dto -> {
										boolean isConcluido = SituacaoEventoEdocsEnum.CONCLUIDO.getValue().equals(dto.situacao());
										if (!isConcluido) {
											logger.warn("Status não concluído: {}", dto.situacao()); // Log de status inesperado
										}
										return isConcluido;
									})
									.repeatWhenEmpty( flux -> flux.delayElements(Duration.ofSeconds(2))) // Repete a cada 2s se vazio
									.timeout(Duration.ofMinutes(1)) // Timeout total em minutos
									.switchIfEmpty(Mono.error(new RuntimeException("Falha ao consultar situacao do evento de CAPTURA ID " + idEventoCaptura + ".")))
									.doOnSuccess( situacaoEventoDto -> logger.info("Captura confirmada: {}", situacaoEventoDto.situacao() ))
									.doOnError( e -> logger.error("Falha ao verificar situacao do evento de caputura do arquivo para o servidor S3 do E-Docs.", e ) )
								.flatMap( dtoSituacaoEvento -> 
									FeignReativo.fromFeign( () ->  
										autuarProcesso( 
											idProjeto,
											token,
											dtoSituacaoEvento.idDocumento() 
										)
									)
										.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
										.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs.")))
										.doOnSuccess( retornoAutuacao -> logger.info("Chamada ao endpoint realizada: {}", retornoAutuacao ))
										.doOnError( e -> logger.error("Falha ao executar chamada ao endpoint para autuar um processo via E-Docs. {}", e ) )
									.flatMap( idEventoAutuar -> 
										FeignReativo.fromFeign( () ->  
											consultarSituacaoEventoEdocs( 
												idEventoAutuar.replace("\"", ""), 
												token  
											))
											.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
											.doOnNext(dto -> logger.info("Status recebido: {}", dto.situacao())) // LOG PARA DEBUG
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
											.doOnSuccess( situacaoEventoDto -> logger.info("Autuacaao confirmada: {}", situacaoEventoDto.situacao() ))
											.doOnError( e -> logger.error("Falha ao verificar situacao do evento de autuacao do processo no E-Docs.", e ) )
										.flatMap( dtoSituacaoEventoAutuacao -> 
											FeignReativo.fromFeign( () ->  
												despacharProcessoSUBCAP( 
													idProjeto, 
													token, 
													dtoSituacaoEventoAutuacao.idProcesso() )
											)
											.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
											.switchIfEmpty(Mono.error(new RuntimeException("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs.")))
											.doOnSuccess( retornoDespacho -> logger.info("Chamada ao endpoint realizada: {}", retornoDespacho ))
											.doOnError( e -> logger.error("Falha ao executar chamada ao endpoint para despachar um processo via E-Docs. {}", e ) )
											.flatMap( idEventoDespacho -> 
												FeignReativo.fromFeign( () ->  
													consultarSituacaoEventoEdocs( 
														idEventoDespacho.replace("\"", ""), 
														token  
													))
													.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
													.doOnNext(dto -> logger.info("Status recebido: {}", dto.situacao())) // LOG PARA DEBUG
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
													.doOnSuccess( situacaoEventoDto -> logger.info("Despacho confirmado: {}", situacaoEventoDto.situacao() ))
													.doOnError( e -> logger.error("Falha ao verificar situacao do evento de despacho do processo no E-Docs.", e ) )
												)
										)
									)
								)
							)
						)
					)
				)
			).thenReturn("Upload concluído com sucesso");

	}
	
	private Mono<String> buscarTokenReativo() {
		Optional<String> token = AutorizacaoACService.getEdocsToken("accessTokenAC");
		String tokenPuro = "";
		if (token.isPresent()) {
			tokenPuro = token.get();
		}
		return Mono.just(tokenPuro); 
	}

	private SituacaoEventoDto consultarSituacaoEventoEdocs( String idEventoEdocs, String token ){
		logger.info("Iniciar consulta situacao evento id {}.", idEventoEdocs);
		return EdocsWebClient.buscarSituacaoEvento( token, idEventoEdocs );
	}

	private String capturarAssinarDocumento( String identificadorTemporarioArquivo, String nomeArquivo, String token  ){

		logger.info("Iniciar captura e assina DIC com dados do servidor/agente publico logado.");

		String idPapelCapturadorAssinante = "fc4fb210-fb3a-4d51-845c-cfd6921e5aa6"; // TESTE - TEM QUE PEGAR DO USUARIO LOGADO
		String idClasse = "6c6118eb-3129-4dfb-beaf-b16d3acf4ba6"; // TESTE - TEM QUE PEGAR DO USUARIO LOGADO
		boolean credenciarCapturador = true;
		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);
		CapturaAssinaturaBodyDto capturaAssinaturaBodyDto = new CapturaAssinaturaBodyDto( idPapelCapturadorAssinante, idClasse, 
																						  nomeArquivo, credenciarCapturador, 
																						  restricaoAcessoBodyDto, 
																						  identificadorTemporarioArquivo);

		return EdocsWebClient.capturarDocumento( token, capturaAssinaturaBodyDto);

	}

	private String autuarProcesso( Integer idProjeto, String token, String idDocumentoCapturado ){
		
		logger.info("Iniciar autuacao do processo para o projeto id {} - documento id {}.", idProjeto, idDocumentoCapturado );
				
		String idClasse = "bb6509d9-d8f5-46d1-a3c8-3e9ed6318f63";
		String idPapelResponsavel = "fc4fb210-fb3a-4d51-845c-cfd6921e5aa6";
		String idLocal = "d2ab305b-3f41-4802-b509-09f447ab3016";
		String resumo = "Autuação testes uso de API do E-Docs - teste 03/07/2025 - DIC";
		List<String> idsAgentesInteressados = List.of( "e7942272-bb41-4d32-9c51-de3145ebcf21", "d2c2c928-ddcc-4f0a-b58d-0ffcab7f31e3" ) ;
		List<String> idsDocumentosEntranhados = List.of( idDocumentoCapturado );

		AutuarProjetoDto autuarProjetoDto = new AutuarProjetoDto( idClasse, idPapelResponsavel, idLocal, resumo, idsAgentesInteressados, idsDocumentosEntranhados );

		return EdocsWebClient.autuarProcesso( token, autuarProjetoDto );

	}

	private String despacharProcessoSUBCAP( Integer idProjeto, String token, String idProcessoEDocs ){
		
		logger.info("Iniciar depacho do processo para o projeto id {} para SUBCAP.", idProjeto );
		
		String idDestino = "e67022ba-ec5d-4082-9ca1-01979df9c462";
		String mensagem = "TESTE ENVIO PELA API DO SISCAP AUTOMATICAMENTE";
		//String idProcessoEDocs = "bb636bfc-5a36-4001-9cdc-905bdd708e5c";
		String idPapelResponsavel = "fc4fb210-fb3a-4d51-845c-cfd6921e5aa6";
		RestricaoAcessoBodyDto restricaoAcessoBodyDto = new RestricaoAcessoBodyDto(true, null, null);

		DespacharProjetoDto despacharProjetoDto = new DespacharProjetoDto( idDestino, mensagem, restricaoAcessoBodyDto, idProcessoEDocs, idPapelResponsavel );

		return EdocsWebClient.depacharProcesso( token, despacharProjetoDto );

	}


}