package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusProspeccaoEnum {

	PROSPECTADO("Prospectado"),
	NAO_PROSPECTADO("Não Prospectado");

	private final String value;
}