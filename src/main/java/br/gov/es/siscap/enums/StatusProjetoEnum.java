package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusProjetoEnum {

	EM_ELABORACAO("Em Elaboração"),
	EM_ANALISE("Em Análise"),
	ARQUIVADO("Arquivado"),
	PARECER_SEP("Parecer SEP");

	private final String value;
}