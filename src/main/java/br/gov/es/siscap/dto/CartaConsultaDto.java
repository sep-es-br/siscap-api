package br.gov.es.siscap.dto;

import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.models.CartaConsulta;

public record CartaConsultaDto(

			Long id,
			ObjetoOpcoesDto objeto,
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