package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.edocswebapi.AtosProcessoEdocsDto;
import br.gov.es.siscap.dto.edocswebapi.AutuarProjetoDto;
import br.gov.es.siscap.dto.edocswebapi.AvocarProcessoEdocsDto;
import br.gov.es.siscap.dto.edocswebapi.CapturaAssinaturaBodyDto;
import br.gov.es.siscap.dto.edocswebapi.DesentranharArquivoProcessoEdocsDto;
import br.gov.es.siscap.dto.edocswebapi.DespacharProjetoDto;
import br.gov.es.siscap.dto.edocswebapi.EntranharDocumentosProcessoEdocsDto;
import br.gov.es.siscap.dto.edocswebapi.GerarUrlUploadResponseDto;
import br.gov.es.siscap.dto.edocswebapi.ProcessoDocumentosAtoProcessoDto;
import br.gov.es.siscap.dto.edocswebapi.ProcessoEdocsDto;
import br.gov.es.siscap.dto.edocswebapi.ProcessoVinculadoDocumentoDto;
import br.gov.es.siscap.dto.edocswebapi.SituacaoEventoDto;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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

	@GetMapping("/v2/processos/{idProcesso}")
	ProcessoEdocsDto buscarDadosProcessoEdocs( @RequestHeader("Authorization") String bearerToken, 
		@PathVariable String idProcesso );
	
	@PostMapping("/v2/processos/avocar")
	String avocarProcesso( @RequestHeader("Authorization") String bearerToken,
		@RequestBody AvocarProcessoEdocsDto request );

	@PostMapping("/v2/processos/desentranhar")
	String desentranharDocumentosProcesso( @RequestHeader("Authorization") String bearerToken,
		@RequestBody DesentranharArquivoProcessoEdocsDto request );

	@GetMapping("/v2/documentos/{idDocumento}/processos")
	List<ProcessoVinculadoDocumentoDto> buscarProcessosVinculadosDocumento( @RequestHeader("Authorization") String bearerToken, 
		@PathVariable String idDocumento );

	@GetMapping("/v2/processos/{idProcessoEdocs}/atos")
	List<AtosProcessoEdocsDto> buscarAtosProcessoEdocs( @RequestHeader("Authorization") String bearerToken, 
		@PathVariable String idProcessoEdocs );

	@GetMapping("/v2/processos/{idProcessoEdocs}/atos/{idAto}/documentos")
	List<ProcessoDocumentosAtoProcessoDto> buscarDocumentosAtoProcesso( @RequestHeader("Authorization") String bearerToken, 
		@PathVariable String idProcessoEdocs, 
		@PathVariable String idAto );

	@PostMapping("/v2/processos/entranhar-documentos")
	String entranharDocumentosProcesso( @RequestHeader("Authorization") String bearerToken,
		@RequestBody EntranharDocumentosProcessoEdocsDto request );
	
}
