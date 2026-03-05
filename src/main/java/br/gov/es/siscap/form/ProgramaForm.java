package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProgramaOrganizacaoDto;
import br.gov.es.siscap.dto.ValorDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProgramaForm(

			@NotBlank
			@Size(max = 12)
			String sigla,

			@NotBlank
			@Size(max = 150)
			String titulo,

			@NotEmpty
			@Size(min = 1)
			List<ProgramaOrganizacaoDto> programaOrganizacaoList,

			@NotEmpty
			@Size(min = 1)
			List<EquipeDto> equipeCaptacao,

			@NotEmpty
			List<Long> idProjetoPropostoList,

			@Valid
			ValorDto valor,

			@Valid
			BigDecimal percentualCustoAdministrativo,

			@Valid
			BigDecimal valorCalculadoTotal

) {
}