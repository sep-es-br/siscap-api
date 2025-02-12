package br.gov.es.siscap.service;

import br.gov.es.siscap.client.EDocsTreinamentoClient;
import br.gov.es.siscap.dto.edocs.EDocsDocumentoArquivoDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EDocsService {

	private final EDocsTreinamentoClient eDocsTreinamentoClient;
	private final AcessoCidadaoAutorizacaoService ACAuthService;
	private final RelatoriosService relatoriosService;
	private final Logger logger = LogManager.getLogger(EDocsService.class);

	/*
		- CHAMAR RelatoriosService PARA BUSCAR Resource DO DIC
		- UTILIZAR Resource.getFile().getTotalSpace() [OU ALGO ASSIM] PRA PEGAR O TAMANHO DO ARQUIVO
		- ALIMENTAR NA CHAMADA gerarUrlUploadArquivo
		- USAR O MESMO Resource PARA ALIMENTAR REQUISICAO POST USAR URL GERADA EM EDocsDocumentoArquivoDto
		  |-> VER SE DA CERTO; ESTUDAR COMO FAZER ISSO NO JAVA
	*/

//	@PostConstruct
//	public void init() {
//		Resource relatorioTeste = relatoriosService.gerarArquivo("DIC", 56);
//
//		try {
//			int tamanhoArquivoTeste = (int) relatorioTeste.contentLength();
//			EDocsDocumentoArquivoDto documentoArquivoDto = gerarUrlUploadArquivo(tamanhoArquivoTeste);
//		} catch (Exception e) {
//			logger.error("Erro ao gerar arquivo", e);
//		}
//	}

	public EDocsDocumentoArquivoDto gerarUrlUploadArquivo(Integer tamanhoArquivo) {
		return eDocsTreinamentoClient.gerarUrlUploadArquivo(ACAuthService.getAuthorizationHeader(), tamanhoArquivo);
	}

	private void efetuarUploadArquivoNuvem(EDocsDocumentoArquivoDto documentoArquivoDto, Resource relatorio) {
	}
}
