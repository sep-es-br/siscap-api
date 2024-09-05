package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoCidade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RateioCidadeDto(
			Long idCidade,

			@NotNull
			@Positive
			BigDecimal quantia,

			@NotNull
			@Positive
			BigDecimal percentual
) {

	public RateioCidadeDto(ProjetoCidade projetoCidade) {
		this(
					projetoCidade.getCidade().getId(),
					projetoCidade.getQuantia(),
					projetoCidade.getPercentual()
		);
	}
}
