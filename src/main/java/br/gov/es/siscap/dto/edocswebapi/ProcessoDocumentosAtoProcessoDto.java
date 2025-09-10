package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProcessoDocumentosAtoProcessoDto(

    @JsonProperty("documentoNome")
    String documentoNome,

    @JsonProperty("documentoId")
    String documentoId,

    @JsonProperty("termo")
    boolean termo,

    @JsonProperty("sequencial")
    Integer sequencial
    
) { }
