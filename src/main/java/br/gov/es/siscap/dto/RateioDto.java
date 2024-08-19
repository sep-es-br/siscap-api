package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoCidade;

import java.math.BigDecimal;

public record RateioDto(
			Long idCidade,
			BigDecimal quantia
) {

	public RateioDto(ProjetoCidade projetoCidade) {
		this(projetoCidade.getCidade().getId(), projetoCidade.getQuantia());
	}
}
