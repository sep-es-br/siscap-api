package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Programa;

import java.util.List;

public record ProgramaDto(

			Long id,
			String sigla,
			String titulo,
			List<Long> idOrgaoExecutorList,
			List<EquipeDto> equipeCaptacao,
			List<ProjetoPropostoDto> projetosPropostos,
			ValorDto valor
) {

	public ProgramaDto(Programa programa, List<EquipeDto> equipeCaptacao, List<ProjetoPropostoDto> projetosPropostos, ValorDto valor) {
		this(
					programa.getId(),
					programa.getSigla(),
					programa.getTitulo(),
					programa.getOrgaoExecutorSet().stream().map(Organizacao::getId).toList(),
					equipeCaptacao,
					projetosPropostos,
					valor
		);
	}
}
