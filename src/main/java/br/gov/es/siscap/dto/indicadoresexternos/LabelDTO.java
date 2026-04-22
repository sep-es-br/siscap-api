package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

/**
 * Representa um model name (label) e seus valores possíveis.
 */
public record LabelDTO(
        Long id,
        String nome,
        List<String> valores) {
            
}
