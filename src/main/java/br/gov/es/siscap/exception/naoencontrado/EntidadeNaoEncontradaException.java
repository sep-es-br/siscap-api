package br.gov.es.siscap.exception.naoencontrado;

public class EntidadeNaoEncontradaException extends NaoEncontradoException {
    public EntidadeNaoEncontradaException(Long id) {
        super("Não foi encontrada uma entidade com o id [" + id + "]!");
    }
}
