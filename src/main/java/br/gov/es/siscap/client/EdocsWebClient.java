package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.edocswebapi.AutuarProjetoDto;
import br.gov.es.siscap.dto.edocswebapi.CapturaAssinaturaBodyDto;
import br.gov.es.siscap.dto.edocswebapi.DespacharProjetoDto;
import br.gov.es.siscap.dto.edocswebapi.GerarUrlUploadResponseDto;
import br.gov.es.siscap.dto.edocswebapi.SituacaoEventoDto;
import reactor.core.publisher.Mono;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.Optional;

@FeignClient(name = "edocsWeb", url = "${api.edocs.uri.webapi}")
public interface EdocsWebClient {

	@GetMapping("/v2/documentos/upload-arquivo/gerar-url-upload/{tamanhoArquivoEmBytes}")
	GerarUrlUploadResponseDto
	 gerarUrlUploadArquivo( @RequestHeader("Authorization") String authToken, @PathVariable Long tamanhoArquivoEmBytes );
	
	@PostMapping("/v2/documentos/capturar/nato-digital/auto-assinado/servidor")
	String capturarDocumento( @RequestHeader("Authorization") String bearerToken,
		@RequestBody CapturaAssinaturaBodyDto request );

	@GetMapping("/v2/eventos/{idEvento}")
	SituacaoEventoDto buscarSituacaoEvento( @RequestHeader("Authorization") String bearerToken, 
		@PathVariable String idEvento );

	@PostMapping("/v2/processos/autuar")
	String autuarProcesso(  @RequestHeader("Authorization") String bearerToken,
		@RequestBody AutuarProjetoDto request );

	@PostMapping("/v2/processos/despachar")
	String depacharProcesso( @RequestHeader("Authorization") String bearerToken,
		@RequestBody DespacharProjetoDto request );

}
