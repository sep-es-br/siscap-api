package br.gov.es.siscap.infra;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class MensagemErroRest {

    private final HttpStatus status;
    private final Integer codigo;
    private final String mensagem;
    private final List<String> erros;

    public MensagemErroRest(HttpStatus status, String mensagem, List<String> erros) {
        this.status = status;
        this.codigo = status.value();
        this.mensagem = mensagem;
        this.erros = erros;
    }
}
