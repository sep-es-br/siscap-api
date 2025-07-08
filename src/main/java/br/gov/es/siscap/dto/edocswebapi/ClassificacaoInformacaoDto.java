package br.gov.es.siscap.dto.edocswebapi;

public record ClassificacaoInformacaoDto(
    Integer prazoAnos,
    Integer prazoMeses,
    Integer prazoDias,
    String justificativa,
    String idPapelAprovador    
) {}
