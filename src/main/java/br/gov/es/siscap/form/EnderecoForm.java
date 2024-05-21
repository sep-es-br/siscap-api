package br.gov.es.siscap.form;

import jakarta.validation.constraints.NotNull;

public record EnderecoForm(
        String rua,
        String numero,
        String bairro,
        String complemento,
        String codigoPostal,
        @NotNull
        Long idCidade) {
}
