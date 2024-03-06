package br.gov.es.siscap.exception.naoencontrado;

public class ProjetoNaoEncontradoException extends NaoEncontradoException {

    public ProjetoNaoEncontradoException(Long id) {
        super("Não foi encontrado um projeto com o id [" + id + "]!");
    }

}
