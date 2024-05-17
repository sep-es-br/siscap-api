package br.gov.es.siscap.exception.naoencontrado;

public class PessoaNaoEncontradoException extends NaoEncontradoException {
    public PessoaNaoEncontradoException(Long id) {
        super("Não foi encontrada uma pessoa com o id [" + id + "]!");
    }

    public PessoaNaoEncontradoException(String subNovo) {
        super("Não foi encontrada uma pessoa com o sub [" + subNovo + "]!");
    }
}
