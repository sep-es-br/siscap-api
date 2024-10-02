package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoMicrorregiao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RateioMicrorregiaoDto(

			Long idMicrorregiao,

			@NotNull
			@Positive
			BigDecimal quantia,

			@NotNull
			@Positive
			BigDecimal percentual
) {

	public RateioMicrorregiaoDto(ProjetoMicrorregiao projetoMicrorregiao) {
		this(
					projetoMicrorregiao.getMicrorregiao().getId(),
					projetoMicrorregiao.getQuantia(),
					projetoMicrorregiao.getPercentual()
		);
	}
}
