package br.gov.es.siscap.exception;

public class UsuarioSemPermissaoException extends RuntimeException {

    public UsuarioSemPermissaoException() {
        super("Usuário sem permissão.");
    }

}
