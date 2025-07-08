package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.edocswebapi.GerarUrlUploadResponseDto;
import br.gov.es.siscap.dto.edocswebapi.SituacaoEventoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "edocsWeb", url = "${api.edocs.uri.webapi}")
public interface EdocsWebClient {

	@GetMapping("/v2/documentos/upload-arquivo/gerar-url-upload/{tamanhoArquivoEmBytes}")
	GerarUrlUploadResponseDto gerarUrlUploadArquivo( @RequestHeader("Authorization") String authToken, @PathVariable Long tamanhoArquivoEmBytes );
	
	@PostMapping("/v2/documentos/capturar/nato-digital/auto-assinado/servidor")
	void capturarDocumento( @RequestHeader Map<String, Object> headers );

	@GetMapping("/v2/eventos/{idEvento}")
	SituacaoEventoDto buscarSituacaoEvento( @RequestHeader Map<String, Object> headers, @PathVariable String idEvento );

	@PostMapping("/v2/processos/autuar")
	String autuarProcesso( @RequestHeader Map<String, Object> headers );

	@PostMapping("/v2/processos/despachar")
	String depacharProcesso( @RequestHeader Map<String, Object> headers );

}
