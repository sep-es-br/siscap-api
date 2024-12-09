package br.gov.es.siscap.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnderecoForm(

			@NotBlank
			String rua,

			@NotBlank
			String numero,

			@NotBlank
			String bairro,

			@NotBlank
			String complemento,

			@NotBlank
			String codigoPostal,

			@NotNull
			Long idCidade
) {
}
