package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CapturaAssinarDocumentoDto(
        
    @JsonProperty("identificadorTemporarioArquivoNaNuvem")
    String identificadorTemporarioArquivoNaNuvem,

    @JsonProperty("body")
    CapturaAssinaturaBody body
    
    )

{ }
