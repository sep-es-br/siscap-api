package br.gov.es.siscap.infra;

import br.gov.es.siscap.exception.naoencontrado.NaoEncontradoException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NaoEncontradoException.class)
    private ResponseEntity<MensagemErroRest> projetoNaoEncontradoHandler(NaoEncontradoException e) {
        var mensagem = new MensagemErroRest(HttpStatus.NOT_FOUND, "Recurso não encontrado.",
                List.of(e.getMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(ServiceSisCapException.class)
    private ResponseEntity<MensagemErroRest> sisCapServiceHandler(ServiceSisCapException e) {
        var mensagem = new MensagemErroRest(HttpStatus.INTERNAL_SERVER_ERROR,
                "O SisCap API teve problemas ao processar a requisição", e.getErros());
        return montarRetorno(mensagem);
    }

    private ResponseEntity<MensagemErroRest> montarRetorno(MensagemErroRest mensagem) {
        return ResponseEntity.status(mensagem.getStatus()).body(mensagem);
    }

}
