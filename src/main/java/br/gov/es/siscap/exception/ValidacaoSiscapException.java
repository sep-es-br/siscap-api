package br.gov.es.siscap.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidacaoSiscapException extends RuntimeException {
    private final List<String> erros;

    public ValidacaoSiscapException(List<String> erros) {
        this.erros = erros;
    }

}
