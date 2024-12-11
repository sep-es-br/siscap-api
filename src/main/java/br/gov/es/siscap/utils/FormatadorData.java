package br.gov.es.siscap.utils;

import br.gov.es.siscap.enums.FormatoDataEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public abstract class FormatadorData {

	//Exemplo: '20210503' / 'yyyyMMdd'
	private final static DateTimeFormatter formatterSimples = DateTimeFormatter.BASIC_ISO_DATE;

	public final static String DATA_MINIMA = "19000101";
	public final static String DATA_MAXIMA = "99991231";

	public static String format(LocalDateTime data, FormatoDataEnum formato) {
		return data.format(getFormato(formato));
	}

	public static LocalDateTime parseSimples(String data) {
		return LocalDate.parse(data, formatterSimples).atStartOfDay();
	}

	private static DateTimeFormatter getFormato(FormatoDataEnum formato) {
		return new DateTimeFormatterBuilder().appendPattern(formato.getValue()).toFormatter();
	}
}