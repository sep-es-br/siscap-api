package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DesentranharArquivoProcessoEdocsDto(
    
    @JsonProperty("justificativa")
    String justificativa,

    @JsonProperty("restricaoAcesso")
    RestricaoAcessoBodyDto restricaoAcessoBodyDto,

    @JsonProperty("idProcesso")
    String idProcesso,

    @JsonProperty("idPapelResponsavel")
    String idPapelResponsavel,

    @JsonProperty("sequenciais")
    List<String> sequenciais  


) {}
