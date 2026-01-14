package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusParecerEnum {

	PENDENTE(1L),
	ENVIADO(2L),
	CAPTURADO_EDOCS(3L),
	ENTRANHADO_EDOCS(4L);

	private final Long value;

}
