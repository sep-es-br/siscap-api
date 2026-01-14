package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AtosProcessoEdocsDto(

    @JsonProperty("id")
    String id,

    @JsonProperty("dataHora")
    OffsetDateTime dataHora,

    @JsonProperty("papel")
    Papel papel,

    @JsonProperty("tipo")
    Integer tipo,

    @JsonProperty("descricaoTipo")
    String descricaoTipo,

    @JsonProperty("localizacao")
    Localizacao localizacao

) {

    public record Papel(

        @JsonProperty("cargo")
        String cargo,

        @JsonProperty("setor")
        Setor setor,

        @JsonProperty("id")
        UUID id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente,

        @JsonProperty("sistema")
        Sistema sistema

    ) {}

    public record Setor(

        @JsonProperty("organizacao")
        Organizacao organizacao,

        @JsonProperty("sigla")
        String sigla,

        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente

    ) {}

    public record Organizacao(

        @JsonProperty("patriarca")
        Patriarca patriarca,

        @JsonProperty("sigla")
        String sigla,

        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente

    ) {}

    public record Patriarca(

        @JsonProperty("sigla")
        String sigla,

        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente

    ) {}

    public record Sistema(

        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente

    ) {}

    public record Localizacao(

        @JsonProperty("sigla")
        String sigla,

        @JsonProperty("id")
        String id,

        @JsonProperty("nome")
        String nome,

        @JsonProperty("tipoAgente")
        Integer tipoAgente,

        @JsonProperty("descricaoTipoAgente")
        String descricaoTipoAgente

    ) {}

}
