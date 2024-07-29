package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Formulário para cadastrar um novo projeto. Não deve ser usado fora desse contexto.
 *
 * @param sigla
 * @param titulo
 * @param idOrganizacao
 * @param valorEstimado
 * @param idMicrorregioes
 * @param objetivo
 * @param objetivoEspecifico
 * @param situacaoProblema
 * @param solucoesPropostas
 * @param impactos
 * @param arranjosInstitucionais
 */
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

			@NotEmpty
			List<Long> idMicrorregioes,

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

//			@NotEmpty
//			List<Long> idPessoasEquipeElab

			@Positive
			@NotNull
			Long idResponsavelProponente,

			@NotEmpty
			List<EquipeDto> equipeElaboracao
) {
}
