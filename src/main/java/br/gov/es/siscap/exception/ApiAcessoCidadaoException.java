package br.gov.es.siscap.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ApiAcessoCidadaoException extends RuntimeException {

    private final List<String> erros;

    public ApiAcessoCidadaoException(List<String> erros) {
        super("Erro ao comunicar com a API do Acesso Cidadão.");
        this.erros = erros;
    }
}
