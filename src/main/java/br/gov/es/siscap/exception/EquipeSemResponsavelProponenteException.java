package br.gov.es.siscap.exception;

public class EquipeSemResponsavelProponenteException extends RuntimeException {

		public EquipeSemResponsavelProponenteException() {
				super("A equipe não possui um responsável proponente.");
		}
}
