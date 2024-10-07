package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;

public record ProjetoPropostoSelectDto(

			Long id,
			String nome,
			BigDecimal valorEstimado
) {

	public ProjetoPropostoSelectDto(Projeto projeto, ValorDto valorDto) {
		this(
					projeto.getId(),
					(projeto.getSigla() + " - " + projeto.getTitulo()),
					valorDto.quantia()
		);
	}
}
