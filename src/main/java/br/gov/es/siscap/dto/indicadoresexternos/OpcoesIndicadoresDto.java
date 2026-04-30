package br.gov.es.siscap.dto.indicadoresexternos;

import java.math.BigDecimal;
import java.util.List;

public record OpcoesIndicadoresDto(
    Integer idIndicador,
    String nomeIndicador,
    String unidadeMedida,
    String polaridade,
    String medidoPor,
    List<MetasIndicadorExternoDto> metasIndicador,
    Integer maiorAnoInidicador,
    BigDecimal maiorMetaIndicador
) {}