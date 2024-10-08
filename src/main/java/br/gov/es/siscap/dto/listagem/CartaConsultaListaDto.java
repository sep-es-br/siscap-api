package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.CartaConsulta;

import java.time.LocalDateTime;

public record CartaConsultaListaDto(
			Long id,
			String nomeTipoOperacao,
			String nomeObjeto,
			LocalDateTime data
) {

	public CartaConsultaListaDto(CartaConsulta cartaConsulta) {
		this(
					cartaConsulta.getId(),
					cartaConsulta.getTipoOperacao().getTipo(),
					cartaConsulta.getCartaConsultaObjeto().nome(),
					cartaConsulta.getCriadoEm()
		);
	}
}
