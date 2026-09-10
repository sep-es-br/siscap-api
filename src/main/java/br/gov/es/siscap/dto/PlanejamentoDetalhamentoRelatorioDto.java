package br.gov.es.siscap.dto;

import java.math.BigDecimal;

public record PlanejamentoDetalhamentoRelatorioDto(
        String gnd,
        String modalidade,
        String idUso,
        String fonte,
        BigDecimal valor) {

    public String getGnd() {
        return gnd;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getIdUso() {
        return idUso;
    }

    public String getFonte() {
        return fonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

}
