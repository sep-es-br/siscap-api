package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Programa;

import java.util.List;

public record ProgramaDto(

			String sigla,
			String titulo,
			Long idOrgaoExecutor,
			List<EquipeDto> equipeCaptacao,
			List<ProjetoPropostoDto> projetosPropostos,
			ValorDto valor
) {

	public ProgramaDto(Programa programa, List<EquipeDto> equipeCaptacao, List<ProjetoPropostoDto> projetosPropostos, ValorDto valor) {
		this(
					programa.getSigla(),
					programa.getTitulo(),
					programa.getOrgaoExecutor().getId(),
					equipeCaptacao,
					projetosPropostos,
					valor
		);
	}
}
