package br.gov.es.siscap.dto;

import java.math.BigDecimal;

public record PlanejamentoDetalhamentoRelatorioDto(
    String gnd,
    String modalidade,
    String idUso,
    String fonte,
    BigDecimal valor ) {

}
