package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProgramaProjeto;

import java.math.BigDecimal;

public record ProjetoPropostoDto(
			Long idProjeto,
			BigDecimal valor
) {

	public ProjetoPropostoDto(ProgramaProjeto programaProjeto) {
		this(
					programaProjeto.getProjeto().getId(),
					programaProjeto.getValor()
		);
	}
}
