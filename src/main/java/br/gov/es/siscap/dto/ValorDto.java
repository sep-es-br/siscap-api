package br.gov.es.siscap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

import br.gov.es.siscap.validation.groups.ValidacaoEnvio;

public record ValorDto(

			@NotNull( message = "Valor estimado é obrigatório",	groups = ValidacaoEnvio.class )
			@Positive( message = "Valor estimado deve ser maior que zero", groups = ValidacaoEnvio.class )
			BigDecimal quantia,

			@NotNull( message = "Tipo de valor estimado é obrigatório",	groups = ValidacaoEnvio.class )
			@Positive( message = "Tipo de valor estimado deve ser maior que zero", groups = ValidacaoEnvio.class )
			Long tipo,

			@NotBlank( message = "Moeda de valor estimado é obrigatório", groups = ValidacaoEnvio.class )
			@Size(max = 3)
			String moeda

) {
}