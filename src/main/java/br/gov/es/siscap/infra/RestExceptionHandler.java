package br.gov.es.siscap.infra;

import br.gov.es.siscap.exception.ProjetoNaoEncontradoException;
import br.gov.es.siscap.exception.SisCapServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ProjetoNaoEncontradoException.class)
    private ResponseEntity<MensagemErroRest> projetoNaoEncontradoHandler(ProjetoNaoEncontradoException e) {
        var mensagem = new MensagemErroRest(HttpStatus.NOT_FOUND, "Projeto não encontrado",
                List.of(e.getMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(SisCapServiceException.class)
    private ResponseEntity<MensagemErroRest> sisCapServiceHandler(SisCapServiceException e) {
        var mensagem = new MensagemErroRest(HttpStatus.INTERNAL_SERVER_ERROR,
                "O SisCap API teve problemas ao processar a requisição", e.getErros());
        return montarRetorno(mensagem);
    }

    private ResponseEntity<MensagemErroRest> montarRetorno(MensagemErroRest mensagem) {
        return ResponseEntity.status(mensagem.getStatus()).body(mensagem);
    }

}
