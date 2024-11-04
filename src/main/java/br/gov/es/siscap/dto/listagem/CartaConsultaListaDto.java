package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.CartaConsulta;

public record CartaConsultaListaDto(

			Long id,
			String nomeTipoOperacao,
			String nomeObjeto,
			String data
) {

	public CartaConsultaListaDto(CartaConsulta cartaConsulta) {
		this(
					cartaConsulta.getId(),
					cartaConsulta.getTipoOperacao().getTipo(),
					cartaConsulta.getCartaConsultaObjeto().nome(),
					cartaConsulta.formatarDataCartaConsultaListaDto()
		);
	}
}