package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record OpcoesGestaoIndicadorDto(
    long idGestao,
    String nomeGestao,
    List<LabelDTO> labels,
    List<IndicadorDesafioExternoDTO> desafios,
    int doAno,
    int ateAno,
    int deAnoMeta,      // ano inicial calculado para metas
    int ateAnoMeta     // ano final calculado para metas
) {}
