package br.gov.es.siscap.utils;

import br.gov.es.siscap.enums.FormatoDataEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public abstract class FormatadorData {

	public static String formatar(LocalDateTime data, FormatoDataEnum formato) {
		return data.format(getFormato(formato));
	}

	private static DateTimeFormatter getFormato(FormatoDataEnum formato) {
		return new DateTimeFormatterBuilder().appendPattern(formato.getValue()).toFormatter();
	}
}