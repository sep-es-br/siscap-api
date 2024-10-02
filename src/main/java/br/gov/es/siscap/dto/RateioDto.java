package br.gov.es.siscap.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RateioDto(

			@NotEmpty
			@Size(min = 1)
			@Valid
			List<RateioMicrorregiaoDto> rateioMicrorregiao,

			@NotEmpty
			@Size(min = 1)
			@Valid
			List<RateioCidadeDto> rateioCidade
) {
}
