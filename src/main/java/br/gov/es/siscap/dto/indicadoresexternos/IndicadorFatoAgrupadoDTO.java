package br.gov.es.siscap.dto.indicadoresexternos;

import java.math.BigDecimal;
import java.util.List;

public record IndicadorFatoAgrupadoDTO(
    List<MetasIndicadorExternoDto> metas,
    Integer maiorAno,
    BigDecimal maiorMeta
) {}
