package br.gov.es.siscap.exception;

public class EdocsTokenExpiradoException  extends RuntimeException {

    public static final String CODIGO = "EDOCS_TOKEN_EXPIRADO";

    public EdocsTokenExpiradoException(String mensagem) {
        super(mensagem);
    }
    
}
