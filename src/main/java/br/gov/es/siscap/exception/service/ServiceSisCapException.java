package br.gov.es.siscap.exception.service;

import lombok.Getter;

import java.util.List;

@Getter
public class ServiceSisCapException extends RuntimeException {

    private final List<String> erros;

    public ServiceSisCapException(List<String> erros) {
        this.erros = erros;
    }

}



