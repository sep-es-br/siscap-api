package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.edocs.EDocsDocumentoArquivoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "EDocsTreinamentoClient", url = "${api.edocs.uri.treinamento}/v2")
public interface EDocsTreinamentoClient {

	// VER SE PRECISA APLICAR HEADERS DE ACESSOCIDADAO
	@GetMapping("/documentos/upload-arquivo/gerar-url-upload/{tamanhoArquivo}")
	EDocsDocumentoArquivoDto gerarUrlUploadArquivo(@RequestHeader Map<String, Object> headers, @PathVariable Integer tamanhoArquivo);
}
