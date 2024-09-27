package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProgramaValor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ValorDto(
			@NotNull
			@Positive
			BigDecimal quantia,

			@NotNull
			@Positive
			Long tipo,

			@NotBlank
			@Size(max = 3)
			String moeda
) {

	public ValorDto(ProgramaValor programaValor) {
		this(
					programaValor.getQuantia(),
					programaValor.getValor().getId(),
					programaValor.getMoeda()
		);
	}
}