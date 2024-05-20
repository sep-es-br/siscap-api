package br.gov.es.siscap.exception.service;

import lombok.Getter;

import java.util.List;

@Getter
public class SiscapServiceException extends RuntimeException {

    private final List<String> erros;

    public SiscapServiceException(List<String> erros) {
        this.erros = erros;
    }

}



