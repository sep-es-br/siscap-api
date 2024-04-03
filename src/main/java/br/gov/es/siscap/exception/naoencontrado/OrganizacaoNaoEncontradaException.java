package br.gov.es.siscap.exception.naoencontrado;

public class OrganizacaoNaoEncontradaException extends NaoEncontradoException {
    public OrganizacaoNaoEncontradaException(Long id) {
        super("Não foi encontrada uma organização com o id [" + id + "]!");
    }
}
