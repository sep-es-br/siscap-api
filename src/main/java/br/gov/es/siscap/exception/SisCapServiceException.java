package br.gov.es.siscap.exception;

import java.util.List;

public class SisCapServiceException extends RuntimeException {
    public SisCapServiceException(List<String> erros) {
        super(erros.toString());
    }
}
