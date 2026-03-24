package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PapelOrgaoProgramaEnum {

	GESTOR(1),
	EXECUTOR(2);

	private final Integer value;
}