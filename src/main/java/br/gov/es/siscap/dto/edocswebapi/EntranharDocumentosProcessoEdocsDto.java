package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EntranharDocumentosProcessoEdocsDto(

    @JsonProperty("justificativa")
    String justificativa,
    
    @JsonProperty("idsDocumentosEntranhados")
    String[] idsDocumentosEntranhados,

    @JsonProperty("restricaoAcesso")
    RestricaoAcessoBodyDto restricaoAcessoBodyDto,

    @JsonProperty("idProcesso")
    String idProcesso,

    @JsonProperty("idPapelResponsavel")
    String idPapelResponsavel

) {}
