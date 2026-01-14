package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocalCustodiaProcessoEdocsDto(
        @JsonProperty("setor") Setor setor,
        @JsonProperty("id") String id,
        @JsonProperty("nome") String nome,
        @JsonProperty("tipoAgente") int tipoAgente,
        @JsonProperty("descricaoTipoAgente") String descricaoTipoAgente
) {

    public record Setor(
            @JsonProperty("organizacao") Organizacao organizacao,
            @JsonProperty("sigla") String sigla,
            @JsonProperty("id") String id,
            @JsonProperty("nome") String nome,
            @JsonProperty("tipoAgente") int tipoAgente,
            @JsonProperty("descricaoTipoAgente") String descricaoTipoAgente
    ) {

        public record Organizacao(
                @JsonProperty("patriarca") Patriarca patriarca,
                @JsonProperty("sigla") String sigla,
                @JsonProperty("id") String id,
                @JsonProperty("nome") String nome,
                @JsonProperty("tipoAgente") int tipoAgente,
                @JsonProperty("descricaoTipoAgente") String descricaoTipoAgente
        ) {

            public record Patriarca(
                    @JsonProperty("sigla") String sigla,
                    @JsonProperty("id") String id,
                    @JsonProperty("nome") String nome,
                    @JsonProperty("tipoAgente") int tipoAgente,
                    @JsonProperty("descricaoTipoAgente") String descricaoTipoAgente
            ) {}
        }
    }
}