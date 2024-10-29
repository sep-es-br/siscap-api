package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoValorEnum {

	ESTIMADO(1L),
	EM_CAPTACAO(2L),
	CAPTADO(3L),
	CONTRATADO(4L),
	CONTRA_PARTIDA(5L),
	ORCADO(6L),
	EMPENHADO(7L),
	RESERVADO(8L),
	LIQUIDADO(9L),
	PAGO(10L);

	private final Long value;
}