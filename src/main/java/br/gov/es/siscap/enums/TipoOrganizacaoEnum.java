package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoOrganizacaoEnum {

	INSTITUICAO_PUBLICA(1L),
	INSTITUICAO_FINANCEIRA(2L),
	AUTARQUIA(3L),
	EMPRESA_PRIVADA(4L),
	ONG(5L);

	private final Long value;
}
