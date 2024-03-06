package br.gov.es.siscap.form;

public record EnderecoForm(
        String rua,
        Integer numero,
        String bairro,
        String complemento,
        String codigoPostal,
        Long idCidade) {
}
