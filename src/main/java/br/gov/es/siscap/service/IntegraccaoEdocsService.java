package br.gov.es.siscap.service;

import br.gov.es.siscap.client.EdocsWebClient;
import br.gov.es.siscap.dto.edocswebapi.GerarUrlUploadResponseDto;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

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

		logger.info("Efetuando Autuacao Despacho projeto {}", idProjeto);

		Long tamanhoArquivoEmBytes;
		try {
			tamanhoArquivoEmBytes = arquivoDic.contentLength();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Erro ao buscar arquivo DIC para Autuacao.");
		}

		logger.info("Arquivo: tamanho: {}", tamanhoArquivoEmBytes);
		
		Optional<String> token = AutorizacaoACService.getEdocsToken("accessTokenAC");

		logger.info("Gerar Url para Upload do DIC no servidro S3 do E-docs - Projeto {}", idProjeto );
		GerarUrlUploadResponseDto gerarUrlUploadArquivo = EdocsWebClient.gerarUrlUploadArquivo( token.orElse(""), tamanhoArquivoEmBytes );
		logger.info("Url temporaria gerada : ", gerarUrlUploadArquivo.body().toString() );

		if (UploadS3Service != null) {
			
			if ( UploadS3Service.enviarArquivoParaS3OkHttp( gerarUrlUploadArquivo.url(), gerarUrlUploadArquivo.body(), arquivoDic, nomeArquivo, token.orElse("") ) ){
				
				logger.info("Arquivo enviado para o servidor S3 do E-docs - Projeto {}", idProjeto);
				
				logger.info("Realizar captura e assinar DIC com o servidor logado.");
				EdocsWebClient.capturarDocumento(null);	

			}

		}
		
		return;

	}

}