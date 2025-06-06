package br.gov.es.siscap.exception;

public class ValorEstimadoIncompativelAcoesProjetoException extends RuntimeException {

    public ValorEstimadoIncompativelAcoesProjetoException() {
        super("Somatório valores ações do projeto incompatível com valor estimado definido para o projeto.");
    }

}
