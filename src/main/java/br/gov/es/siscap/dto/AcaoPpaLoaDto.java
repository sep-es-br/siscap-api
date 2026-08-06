package br.gov.es.siscap.dto;

import java.math.BigDecimal;
import java.util.List;

public record AcaoPpaLoaDto(
    
    Long id,

    String codigoOrgao,
    String siglaOrgao,
    String nomeOrgao,

    String codigoUnidadeOrcamentaria,
    String siglaUnidadeOrcamentaria,
    String nomeUnidadeOrcamentaria,

    String codigoPrograma,
    String nomePrograma,

    String codigoAcao,
    String nomeAcao,

    String codigoFuncao,
    String nomeFuncao,

    BigDecimal valorPpa,
    BigDecimal valorLoa,

    String anoAcao,

    List<DetalhamentoOrcamentarioLoaDto> detalhamentosLoa
    
) {

}
