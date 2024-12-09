package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.enums.FormatoDataEnum;
import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.utils.FormatadorData;

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
					FormatadorData.formatar(cartaConsulta.getCriadoEm(), FormatoDataEnum.SIMPLES)
		);
	}
}