package br.gov.es.siscap.utils;

public abstract class PreencherZerosEsquerda {

	private static final int LENGTH = 4;

	public static String preencher(String inputString) {
		if (inputString.length() >= LENGTH) return inputString;

		StringBuilder stringBuilder = new StringBuilder();

		while (stringBuilder.length() < LENGTH - inputString.length()) {
			stringBuilder.append('0');
		}

		stringBuilder.append(inputString);

		return stringBuilder.toString();
	}
}
