package br.gov.es.siscap.dto.edocswebapi;

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
    String[] sequenciais 


) {}
