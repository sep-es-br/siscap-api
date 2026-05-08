package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record FiltroIndicadorDto(
    Long idGestao,
    List<FiltroLabelDto> labels,
    List<Long> desafios
) {
}
