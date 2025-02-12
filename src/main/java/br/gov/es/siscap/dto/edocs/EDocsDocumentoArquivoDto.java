package br.gov.es.siscap.dto.edocs;

import java.util.HashMap;

public record EDocsDocumentoArquivoDto(

			String url,
			String identificadorTemporarioArquivoNaNuvem,
			// ENTENDER MELHOR PARAMETRO BODY DO MODELO DOCUMENTOARQUIVO DA API
			HashMap<String, String> body
) {
}
