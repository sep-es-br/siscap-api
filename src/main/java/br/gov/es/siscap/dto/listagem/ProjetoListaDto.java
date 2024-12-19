package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;

public record ProjetoListaDto(

			Long id,
			String sigla,
			String titulo,
			String status,
			BigDecimal valorEstimado,
			boolean isRascunho
) {
	public ProjetoListaDto(Projeto projeto, BigDecimal valorEstimado) {
		this(
					projeto.getId(),
					projeto.getSigla(),
					projeto.getTitulo(),
					projeto.getStatus(),
					valorEstimado,
					projeto.isRascunho()
		);
	}
}