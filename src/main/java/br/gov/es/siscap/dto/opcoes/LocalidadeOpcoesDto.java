package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.models.Localidade;

public record LocalidadeOpcoesDto(

			Long id,
			String nome,
			String tipo,
			Long idLocalidadePai
) {

	public LocalidadeOpcoesDto(Localidade localidade) {
		this(
					localidade.getId(),
					localidade.getNome(),
					localidade.getTipo(),
					localidade.getLocalidadePai() != null ? localidade.getLocalidadePai().getId() : null
		);
	}
}