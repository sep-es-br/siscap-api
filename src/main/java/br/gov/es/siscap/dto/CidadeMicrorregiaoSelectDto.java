package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Cidade;

public record CidadeMicrorregiaoSelectDto(

			Long id,
			String nome,
			Long idMicrorregiao
) {

	public CidadeMicrorregiaoSelectDto(Cidade cidade) {
		this(
					cidade.getId(),
					cidade.getNome(),
					cidade.getMicrorregiao().getId()
		);
	}
}
