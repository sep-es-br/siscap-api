package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

public record LabelDTO(
        Integer idLabel,
        String nome,
        Integer ordem,
        List<LabelValorDTO> valores ) {
}
