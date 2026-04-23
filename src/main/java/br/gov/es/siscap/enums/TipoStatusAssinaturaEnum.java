package br.gov.es.siscap.enums;

public enum TipoStatusAssinaturaEnum {
	PENDENTE(1),
	ASSINADO(2),
	ERRO(3),
	RECUSOUSEASSINAR(4);

	private final int value;

	TipoStatusAssinaturaEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
