package br.gov.es.siscap.exception;

public class UsuarioSemAutorizacaoException extends RuntimeException{

    public UsuarioSemAutorizacaoException() {
        super("Usuário sem autorização para essa funcionalidade.");
    }

}
