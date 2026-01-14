package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DespacharProjetoDto(

    @JsonProperty("idDestino")
    String idDestino,

    @JsonProperty("mensagem")
    String mensagem,

    @JsonProperty("restricaoAcesso")
    RestricaoAcessoBodyDto restricaoAcesso,

    @JsonProperty("idProcesso")
    String idProcesso,

    @JsonProperty("idPapelResponsavel")
    String idPapelResponsavel
    
) {}
