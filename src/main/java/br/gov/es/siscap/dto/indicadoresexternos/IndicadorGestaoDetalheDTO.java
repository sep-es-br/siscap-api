package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record IndicadorGestaoDetalheDTO(
        Long id,
        String nome,
        Boolean ativa,
        String modelLabel,
        List<LabelDTO> labels) {

}
