package br.gov.es.siscap.dto;

import java.math.BigDecimal;
import java.util.List;

public record AcaoPpaLoaDto(
    Long id,
    String codigo,
    String titulo,
    String descricao,
    String unidadeOrcamentaria,
    String orgao,
    String funcao,
    String programa,
    String periodoPpa,
    BigDecimal valorPpa,
    Integer anoLoa,
    BigDecimal valorLoa,
    List<DetalhamentoOrcamentarioLoaDto> detalhamentoOrcamentarioLoa
) {

}
