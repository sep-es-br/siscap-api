package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormatoDataEnum {

	SIMPLES("dd/MM/yyyy"),
	COMPLETO("dd/MM/yyyy - HH:mm");

	private final String value;
}