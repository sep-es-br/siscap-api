package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record OpcoesGestaoIndicadorDto(
    long idGestao,
    String nomeGestao,
    List<LabelDTO> labels
) {}
