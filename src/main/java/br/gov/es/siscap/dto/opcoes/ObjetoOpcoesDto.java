package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.Projeto;

public record ObjetoOpcoesDto(

			Long id,
			String nome,
			String tipo
) {
	public ObjetoOpcoesDto(Projeto projeto) {
		this(
					projeto.getId(),
					(projeto.getSigla() + " - " + projeto.getTitulo()),
					"Projeto"
		);
	}

	public ObjetoOpcoesDto(Programa programa) {
		this(
					programa.getId(),
					(programa.getSigla() + " - " + programa.getTitulo()),
					"Programa"
		);
	}
}