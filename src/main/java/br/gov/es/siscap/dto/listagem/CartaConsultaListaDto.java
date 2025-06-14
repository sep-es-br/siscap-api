package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.enums.FormatoDataEnum;
import br.gov.es.siscap.models.CartaConsulta;
import br.gov.es.siscap.utils.FormatadorData;

public record CartaConsultaListaDto(

			Long id,
			String codigoCartaConsulta,
			String nomeTipoOperacao,
			String nomeObjeto,
			String data,
			boolean prospectado
) {

	public CartaConsultaListaDto(CartaConsulta cartaConsulta) {
		this(
					cartaConsulta.getId(),
					cartaConsulta.gerarCodigoCartaConsulta(),
					cartaConsulta.getTipoOperacao().getTipo(),
					cartaConsulta.getCartaConsultaObjeto().nome(),
					FormatadorData.format(cartaConsulta.getCriadoEm(), FormatoDataEnum.SIMPLES),
					cartaConsulta.isProspectado()
		);
	}
}