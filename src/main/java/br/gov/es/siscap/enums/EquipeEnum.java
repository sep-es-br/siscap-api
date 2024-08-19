package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EquipeEnum {

	ELABORACAO(1L),
	EXECUCAO(2L),
	CAPTACAO(3L),
	MONITORAMENTO(4L);

	private final Long value;
}
