package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;

public record ProjetoPropostoOpcoesDto(

			Long id,
			String nome,
			BigDecimal valorEstimado,
			Long idPrograma
) {

	public ProjetoPropostoOpcoesDto(Projeto projeto, ValorDto valorDto) {
		this(
					projeto.getId(),
					(projeto.getSigla() + " - " + projeto.getTitulo()),
					valorDto.quantia(),
					projeto.getPrograma() != null ? projeto.getPrograma().getId() : null
		);
	}
}