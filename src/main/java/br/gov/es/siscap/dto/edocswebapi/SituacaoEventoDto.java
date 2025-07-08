package br.gov.es.siscap.dto.edocswebapi;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SituacaoEventoDto(

    @JsonProperty("id")
    String id,

    @JsonProperty("idCidadao")
    String idCidadao,

    @JsonProperty("situacao")
    String situacao,

    @JsonProperty("criacao")
    OffsetDateTime criacao,

    @JsonProperty("conclusao")
    OffsetDateTime conclusao,

    @JsonProperty("tipo")
    String tipo,

    @JsonProperty("idAto")
    String idAto,

    @JsonProperty("idTermo")
    String idTermo

    ) { }
