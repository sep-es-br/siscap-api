package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record FiltroLabelDto(
    Long idLabel,
    List<Long> idLabelValores
) {
}
