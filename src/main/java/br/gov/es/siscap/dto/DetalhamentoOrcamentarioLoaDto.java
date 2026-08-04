package br.gov.es.siscap.dto;

import java.math.BigDecimal;

public record DetalhamentoOrcamentarioLoaDto(
    String codigoGnd,
    String codigoModalidade,
    String idUso,
    String fonte,
    BigDecimal valor
) {
}
