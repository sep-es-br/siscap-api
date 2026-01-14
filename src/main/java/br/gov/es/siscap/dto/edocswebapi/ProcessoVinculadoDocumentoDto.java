package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProcessoVinculadoDocumentoDto(

    @JsonProperty("id")
    String id,

    @JsonProperty("ano")
    String ano,

    @JsonProperty("protocolo")
    String protocolo,

    @JsonProperty("resumo")
    String resumo,

    @JsonProperty("situacao")
    String situacao
    
    ) { }
