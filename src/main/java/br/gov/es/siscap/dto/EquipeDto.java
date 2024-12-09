package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProgramaPessoa;
import br.gov.es.siscap.models.ProjetoPessoa;

public record EquipeDto(

			Long idPessoa,
			Long idPapel,
			Long idStatus,
			String justificativa
) {

	public EquipeDto(ProjetoPessoa projetoPessoa) {
		this(
					projetoPessoa.getPessoa().getId(),
					projetoPessoa.getTipoPapel().getId(),
					projetoPessoa.getTipoStatus().getId(),
					projetoPessoa.getJustificativa()
		);
	}

	public EquipeDto(ProgramaPessoa programaPessoa) {
		this(
					programaPessoa.getPessoa().getId(),
					programaPessoa.getTipoPapel().getId(),
					programaPessoa.getTipoStatus().getId(),
					programaPessoa.getJustificativa()
		);
	}
}