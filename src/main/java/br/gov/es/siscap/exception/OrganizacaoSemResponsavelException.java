package br.gov.es.siscap.exception;

public class OrganizacaoSemResponsavelException extends RuntimeException{

	public OrganizacaoSemResponsavelException() {
		super("A organização não possui responsável.");
	}
}
