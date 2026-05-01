package br.gov.es.siscap.dto.indicadoresexternos;

import java.math.BigDecimal;

public record MetasIndicadorExternoDto(
    Long idFato,
    Integer anoMeta,
    BigDecimal valorMeta
) {}