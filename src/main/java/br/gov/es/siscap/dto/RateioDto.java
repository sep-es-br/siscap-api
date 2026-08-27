package br.gov.es.siscap.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import br.gov.es.siscap.validation.groups.ValidacaoEnvio;

public record RateioDto(

	Long idLocalidade,

	@NotNull(message = "Percentual do rateio é obrigatório", groups = ValidacaoEnvio.class) 
	@Positive(message = "Percentual do rateio deve ser maior que zero", groups = ValidacaoEnvio.class) 
	BigDecimal percentual,

	@NotNull(message = "Quantia do rateio é obrigatório", groups = ValidacaoEnvio.class) 
	@Positive(message = "Quantia do rateio deve ser maior que zero", groups = ValidacaoEnvio.class) 
	BigDecimal quantia

) {
}