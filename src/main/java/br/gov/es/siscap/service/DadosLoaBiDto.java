package br.gov.es.siscap.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record DadosLoaBiDto(

    @JsonProperty("orgao")
    String orgao,

    @JsonProperty("sigla")
    String sigla,

    @JsonProperty("nom_orgao")
    String nomeOrgao,

    @JsonProperty("uo")
    String unidadeOrcamentaria,

    @JsonProperty("mne_uo")
    String siglaUnidadeOrcamentaria,

    @JsonProperty("NOM_UO")
    String nomeUnidadeOrcamentaria,

    @JsonProperty("COD_PROGRAMA")
    String codigoPrograma,

    @JsonProperty("NOM_PROGRAMA")
    String nomePrograma,

    @JsonProperty("acao")
    String codigoAcao,

    @JsonProperty("NOM_ACAO")
    String nomeAcao,

    @JsonProperty("grupo")
    String codigoGrupoDespesa,

    @JsonProperty("NOM_GRUPO_DESPESA")
    String nomeGrupoDespesa,

    @JsonProperty("SGL_GRUPO_DESPESA")
    String siglaGrupoDespesa,

    @JsonProperty("modalidade")
    String codigoModalidade,

    @JsonProperty("iduso")
    String idUso,

    @JsonProperty("fonte")
    String fonte,

    @JsonProperty("loa_fin")
    BigDecimal valorLoa

) {
}
