package br.gov.es.siscap.form;

import java.math.BigInteger;

public record ProjetoForm(
        String sigla,
        String titulo,
        BigInteger valorEstimado,
        String objetivo,
        String objetivoEspecifico) {
}
