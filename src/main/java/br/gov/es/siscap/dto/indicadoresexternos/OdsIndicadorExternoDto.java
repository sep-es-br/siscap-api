package br.gov.es.siscap.dto.indicadoresexternos;

public record OdsIndicadorExternoDto(
    Integer IndicadorId,
    Integer odsId,
    Integer odsOrdem,
    String odsNome,
    String odsDescricao,
    String odsCor
) {
}
