package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RestricaoAcessoBodyDto(
    @JsonProperty("transparenciaAtiva")
    Boolean transparenciaAtiva,
    @JsonProperty("idsFundamentosLegais")
    List<String> idsFundamentosLegais,
    @JsonProperty("classificacaoInformacaoDto")
    ClassificacaoInformacaoDto classificacaoInformacaoDto
) {}
