package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

/**
 * DTO de resposta para o frontend contendo os dados completos de uma gestão,
 * incluindo seus model names (labels) e os valores de cada label.
 *
 * Estrutura:
 * {
 * "id": 1,
 * "nome": "Gestão 2023-26",
 * "ativa": true,
 * "modelLabel": "Eixo, Área Temática",
 * "labels": [
 * {
 * "nome": "Eixo",
 * "valores": ["Eixo 1: +Qualidade de vida", "Eixo 2: +Des. com
 * sustentabilidade"]
 * },
 * {
 * "nome": "Área Temática",
 * "valores": ["Saúde", "Educação", ...]
 * }
 * ]
 * }
 */
public record IndicadorGestaoDetalheDTO(
        Long id,
        String nome,
        Boolean ativa,
        String modelLabel,
        List<LabelDTO> labels) {

}
