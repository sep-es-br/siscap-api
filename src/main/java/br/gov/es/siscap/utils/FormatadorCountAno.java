package br.gov.es.siscap.utils;

import java.time.LocalDate;

public abstract class FormatadorCountAno {

	public static String formatar(int contagemAnoAtual) {

		String contagemAnoAtualStr = (contagemAnoAtual + 1) + "";
		String contagemAnoAtualPadded = String.format("%0" + 4 + "d", Integer.parseInt(contagemAnoAtualStr));
		String anoAtual = LocalDate.now().getYear() + "";

		return contagemAnoAtualPadded + "/" + anoAtual;
	}
}
