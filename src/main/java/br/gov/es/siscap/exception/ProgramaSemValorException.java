package br.gov.es.siscap.exception;

public class ProgramaSemValorException extends RuntimeException {
	public ProgramaSemValorException() {
		super("O programa não possui um valor atrelado.");
	}
}
