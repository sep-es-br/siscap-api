package br.gov.es.siscap.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class SisCapServiceException extends RuntimeException {

    private final List<String> erros;

    public SisCapServiceException(List<String> erros) {
        this.erros = erros;
    }

}



