package br.gov.es.siscap.infra;

import br.gov.es.siscap.exception.ApiAcessoCidadaoException;
import br.gov.es.siscap.exception.UsuarioSemAutorizacaoException;
import br.gov.es.siscap.exception.UsuarioSemPermissaoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.NaoEncontradoException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(NaoEncontradoException.class)
    private ResponseEntity<MensagemErroRest> projetoNaoEncontradoHandler(NaoEncontradoException e) {
        var mensagem = new MensagemErroRest(HttpStatus.NOT_FOUND, "Recurso não encontrado.",
                List.of(e.getMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(SiscapServiceException.class)
    private ResponseEntity<MensagemErroRest> sisCapServiceHandler(SiscapServiceException e) {
        var mensagem = new MensagemErroRest(HttpStatus.INTERNAL_SERVER_ERROR,
                "O SisCap API teve problemas ao processar a requisição", e.getErros());
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<MensagemErroRest> dataIntegrityViolationHandler(DataIntegrityViolationException e) {
        var mensagem = new MensagemErroRest(HttpStatus.BAD_REQUEST,
                "O SisCap API identificou violação de integridade na base de dados",
                Collections.singletonList("Por favor, contate o suporte."));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<MensagemErroRest> methodArgumentNotValidHandler(MethodArgumentNotValidException exception) {
        List<String> errorMessage = exception.getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage()).toList();
        var mensagem = new MensagemErroRest(HttpStatus.BAD_REQUEST,
                "Erro ", errorMessage);
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<MensagemErroRest> noResourceFounddHandler(NoResourceFoundException exception) {
        var mensagem = new MensagemErroRest(HttpStatus.NOT_FOUND,
                "Recurso não encontrado", Collections.singletonList("/" + exception.getResourcePath() + " não existe."));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(ValidacaoSiscapException.class)
    private ResponseEntity<MensagemErroRest> validacaoSiscapHandler(ValidacaoSiscapException exception) {
        var mensagem = new MensagemErroRest(HttpStatus.BAD_REQUEST, "Existem alguns problemas com o cadastro.",
                exception.getErros());
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(UsuarioSemPermissaoException.class)
    private ResponseEntity<MensagemErroRest> usuarioSemPermissaoHandler(UsuarioSemPermissaoException exception) {
        var mensagem = new MensagemErroRest(HttpStatus.UNAUTHORIZED, "Acesso negado",
                Collections.singletonList(exception.getMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(UsuarioSemAutorizacaoException.class)
    private ResponseEntity<MensagemErroRest> usuarioSemAutorizacaoHandler(UsuarioSemAutorizacaoException exception) {
        var mensagem = new MensagemErroRest(HttpStatus.FORBIDDEN, "Usuário sem autorização",
                Collections.singletonList(exception.getMessage()));
        return montarRetorno(mensagem);
    }

    @ExceptionHandler(ApiAcessoCidadaoException.class)
    private ResponseEntity<MensagemErroRest> apiAcessoCidadaoHandler(ApiAcessoCidadaoException e) {
        var mensagem = new MensagemErroRest(HttpStatus.FORBIDDEN, e.getMessage(), e.getErros());
        return montarRetorno(mensagem);
    }

    private ResponseEntity<MensagemErroRest> montarRetorno(MensagemErroRest mensagem) {
        logger.error(mensagem);
        return ResponseEntity.status(mensagem.status()).body(mensagem);
    }

}
