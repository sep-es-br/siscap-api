package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProspeccaoInteressado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InteressadoDto(

			@NotNull
			@Positive
			Long idInteressado,

			@NotBlank
			String emailInteressado
) {

	public InteressadoDto(ProspeccaoInteressado prospeccaoInteressado) {
		this(
					prospeccaoInteressado.getPessoa().getId(),
					prospeccaoInteressado.getEmailProspeccao()
		);
	}
}