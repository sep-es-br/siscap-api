package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.InteressadoDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ProspeccaoForm(

			@NotNull
			@Positive
			Long idCartaConsulta,

			@NotNull
			@Positive
			Long idOrganizacaoProspectora,

			@NotNull
			@Positive
			Long idPessoaProspectora,

			@NotNull
			@Positive
			Long idOrganizacaoProspectada,

			@NotEmpty
			@Valid
			List<InteressadoDto> interessadosList
) {
}