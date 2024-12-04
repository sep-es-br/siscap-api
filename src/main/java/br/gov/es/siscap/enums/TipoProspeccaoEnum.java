package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoProspeccaoEnum {

	ENVIO("Envio"),
	RECEBIMENTO("Recebimento");

	private final String value;
}