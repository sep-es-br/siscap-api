package br.gov.es.siscap.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoAcoesPlanejamentoProjetoRelatorio {

    private Boolean naoPrevistoPpa;

    private String periodoPlanejamento;

    private String codigoAcao;

    private String nomeAcao;

    private String unidadeOrcamentaria;

    private String orgao;

    private String funcao;

    private String programa;

    private BigDecimal valorPpa;

    private String anoLoa;

    private BigDecimal valorLoa;

    private List<PlanejamentoDetalhamentoRelatorioDto> detalhamentos;
    

}
