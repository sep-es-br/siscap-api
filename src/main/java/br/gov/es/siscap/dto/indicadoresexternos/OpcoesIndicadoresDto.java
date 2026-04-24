package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record OpcoesIndicadoresDto(
    Integer idIndicador,
    String nomeIndicador,
    String unidadeMedida,
    String polaridade,
    String medidoPor
    // IndicadorGestaoResumoDTO gestao,
    // DesafioDTO desafio,
    // List<LabelDTO> labels
) {}
