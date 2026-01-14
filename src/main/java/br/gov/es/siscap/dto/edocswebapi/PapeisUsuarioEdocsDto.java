package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PapeisUsuarioEdocsDto(

    @JsonProperty("id")
    String id,

    @JsonProperty("nome")
    String nome,

    @JsonProperty("nomeServidorPapel")
    String nomeServidorPapel,

    @JsonProperty("tipoLocalizacaoPapel")
    String tipoLocalizacaoPapel,

    @JsonProperty("nomeUnidadePapel")
    String nomeUnidadePapel,

    @JsonProperty("tipo")
    String tipo,

    @JsonProperty("siglaUnidadePapel")
    String siglaUnidadePapel,

    @JsonProperty("nomeOrganizacaoPapel")
    String nomeOrganizacaoPapel,

    @JsonProperty("idEncaminhamento")
    String siglaOrganizacaoPapel
    
    ) { }
