package br.gov.es.siscap.dto;

import java.util.List;

import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.models.CartaConsulta;

public record CartaConsultaDto(
		Long id,
		ObjetoOpcoesDto objeto,
		Long operacao,
		String corpo,
		List<CartaConsultaDestinatariosDto> destinatarios) {

	public CartaConsultaDto(CartaConsulta cartaConsulta, String corpo,
			List<CartaConsultaDestinatariosDto> destinatariosCartaConsulta) {
		this(
				cartaConsulta.getId(),
				cartaConsulta.getCartaConsultaObjeto(),
				cartaConsulta.getTipoOperacao().getId(),
				corpo,
				destinatariosCartaConsulta);
	}
}