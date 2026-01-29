package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DadosDocumentoDto(

    @JsonProperty("id")
    String id,

    @JsonProperty("registro")
    String registro,

    @JsonProperty("nome")
    String nome,

    @JsonProperty("extensao")
    String extensao,

    @JsonProperty("nivelAcesso")
    String nivelAcesso
    
    ) { }
