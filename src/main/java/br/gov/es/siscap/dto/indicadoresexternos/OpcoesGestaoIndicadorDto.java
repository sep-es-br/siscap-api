package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

import br.gov.es.siscap.models.IndicadorDesafioExterno;

public record OpcoesGestaoIndicadorDto(
    long idGestao,
    String nomeGestao,
    List<LabelDTO> labels,
    List<IndicadorDesafioExternoDTO> desafios
) {}
