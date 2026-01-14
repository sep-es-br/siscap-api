package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProcessoEdocsDto(
    
@JsonProperty("id")
String id,

@JsonProperty("ano")
String ano,

@JsonProperty("protocolo")
String protocolo,

@JsonProperty("resumo")
String situacao) 
{ }
