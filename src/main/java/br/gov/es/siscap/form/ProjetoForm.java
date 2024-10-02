package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.RateioDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProjetoForm(

			@NotBlank
			@Size(max = 12)
			String sigla,

			@NotBlank
			@Size(max = 150)
			String titulo,

			@NotNull
			@Positive
			Long idOrganizacao,

			@Positive
			@NotNull
			BigDecimal valorEstimado,

			@Valid
			RateioDto rateio,

			@NotBlank
			@Size(max = 2000)
			String objetivo,

			@NotBlank
			@Size(max = 2000)
			String objetivoEspecifico,

			@NotBlank
			@Size(max = 2000)
			String situacaoProblema,

			@NotBlank
			@Size(max = 2000)
			String solucoesPropostas,

			@NotBlank
			@Size(max = 2000)
			String impactos,

			@NotBlank
			@Size(max = 2000)
			String arranjosInstitucionais,

			@Positive
			@NotNull
			Long idResponsavelProponente,

			@NotEmpty
			List<EquipeDto> equipeElaboracao
) {
}
