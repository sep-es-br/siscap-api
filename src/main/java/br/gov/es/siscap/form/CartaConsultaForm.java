package br.gov.es.siscap.form;

import java.util.List;

import br.gov.es.siscap.dto.CartaConsultaDestinatariosDto;
import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartaConsultaForm(
			
			@NotNull
			@Valid
			ObjetoOpcoesDto objeto,

			@Positive
			Long operacao,

			@NotNull
			@NotBlank
			String corpo,

			@Valid
			@NotNull
			@NotEmpty
			List<CartaConsultaDestinatariosDto> destinatarios

) {
}