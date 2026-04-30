package br.gov.es.siscap.dto.indicadoresexternos;

import java.math.BigDecimal;

public record MetasIndicadorExternoDto(
    Integer ano,
    BigDecimal valorMeta
) {}