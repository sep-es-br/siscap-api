package br.gov.es.siscap.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidacaoSiscapException extends RuntimeException {
    private final List<String> erros;

    public ValidacaoSiscapException(List<String> erros) {
        super(erros != null && !erros.isEmpty()
                ? String.join("; ", erros)
                : null);
        this.erros = erros;
    }

    @Override
    public String getMessage() {
        if (erros == null || erros.isEmpty()) {
            return super.getMessage();
        }
        return String.join("; ", erros);
    }

}
