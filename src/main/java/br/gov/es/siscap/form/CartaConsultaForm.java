package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.ObjetoSelectDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartaConsultaForm(

			@Valid
			ObjetoSelectDto objeto,

			@NotNull
			@Positive
			Long operacao,

			@NotBlank
			String corpo
) {
}
