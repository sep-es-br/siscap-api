package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.CartaConsulta;

public record CartaConsultaDto(
			Long id,
			ObjetoSelectDto objeto,
			Long operacao,
			String corpo
) {

	public CartaConsultaDto(CartaConsulta cartaConsulta, String corpo) {
		this(
					cartaConsulta.getId(),
					cartaConsulta.getCartaConsultaObjeto(),
					cartaConsulta.getTipoOperacao().getId(),
					corpo
		);
	}
}
