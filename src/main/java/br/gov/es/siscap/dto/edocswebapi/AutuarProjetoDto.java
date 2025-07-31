package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AutuarProjetoDto(

    @JsonProperty("idClasse")
    String idClasse,
    
    @JsonProperty("idPapelResponsavel")
    String idPapelResponsavel,
    
    @JsonProperty("idLocal")
    String idLocal,

    @JsonProperty("resumo")
    String resumo,

    @JsonProperty("idsAgentesInteressados")
    List<String> idsAgentesInteressados,

    @JsonProperty("idsDocumentosEntranhados")
    List<String> idsDocumentosEntranhados

) {}
