package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassificacaoInformacaoDto(
    @JsonProperty("prazoAnos")
    Integer prazoAnos,
    @JsonProperty("prazoMeses")
    Integer prazoMeses,
    @JsonProperty("prazoDias")
    Integer prazoDias,
    @JsonProperty("justificativa")
    String justificativa,
    @JsonProperty("idPapelAprovador")
    String idPapelAprovador    
) {}
