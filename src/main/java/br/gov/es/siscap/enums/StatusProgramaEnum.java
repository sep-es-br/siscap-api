package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusProgramaEnum {

	EDICAO(1),
	AGUARDANDOASSINATURAS(2),
	ASSINADO(3),
	AUTUADO(4),
	RECUSADO(5);

	private final int value;



	public static StatusProgramaEnum fromCodigo(int codigo) {
		for (StatusProgramaEnum s : values()) {
			if (s.getValue() == codigo) {
				return s;
			}
		}
		throw new IllegalArgumentException("Status inválido: " + codigo);
	}
}
