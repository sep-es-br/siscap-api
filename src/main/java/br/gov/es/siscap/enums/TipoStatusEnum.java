package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoStatusEnum {

	ATIVO(1L),
	INATIVO(2L),
	EXCLUIDO(3L);

	private final Long value;
}