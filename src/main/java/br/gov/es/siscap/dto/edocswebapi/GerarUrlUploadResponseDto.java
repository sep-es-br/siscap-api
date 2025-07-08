package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GerarUrlUploadResponseDto(
    
    @JsonProperty("url")
    String url,

    @JsonProperty("identificadorTemporarioArquivoNaNuvem")
    String identificadorTemporarioArquivoNaNuvem,

    @JsonProperty("body")
    UploadS3BodyDto body
    
    )

{ }
