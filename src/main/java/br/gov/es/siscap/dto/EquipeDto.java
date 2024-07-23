package br.gov.es.siscap.dto;

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
					projetoPessoa.getPapel().getId(),
					projetoPessoa.getStatus().getId(),
					projetoPessoa.getJustificativa());
	}
}
