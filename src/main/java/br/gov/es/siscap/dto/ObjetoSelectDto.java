package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;

public record ObjetoSelectDto(
			Long id,
			String nome,
			String tipo
) {
	public ObjetoSelectDto(Projeto projeto) {
		this(
					projeto.getId(),
					(projeto.getSigla() + " - " + projeto.getTitulo()),
					"Projeto"
		);
	}

	public ObjetoSelectDto(Programa programa) {
		this(
					programa.getId(),
					(programa.getSigla() + " - " + programa.getTitulo()),
					"Programa"
		);
	}
}
