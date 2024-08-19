package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PapelEnum {

	GERENTE_DE_PROJETO(1L),
	RESPONSAVEL_PROPONENTE(2L),
	PROPONENTE(3L),
	PATROCINADOR(4L),
	MEMBRO_DO_PROJETO(5L);

	private final Long value;
}
