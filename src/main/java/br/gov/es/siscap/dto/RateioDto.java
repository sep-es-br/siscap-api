package br.gov.es.siscap.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RateioDto(

			Long idLocalidade,

			@NotNull
			@Positive
			BigDecimal percentual,

			@NotNull
			@Positive
			BigDecimal quantia
) {
}