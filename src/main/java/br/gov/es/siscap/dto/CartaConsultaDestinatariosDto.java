package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.CartaConsultaDestinatario;

public record CartaConsultaDestinatariosDto(
		Long id,
		Long idCartaConsulta,
		Long idOrganizacao,
		String nomeOrganizacao) {

	public CartaConsultaDestinatariosDto(Long id, Long idCartaConsulta, Long idOrganizacao) {
		this(id, idCartaConsulta, idOrganizacao, null);
	}

	public CartaConsultaDestinatariosDto(CartaConsultaDestinatario cartaConsultaDestinatarios) {
		this(cartaConsultaDestinatarios.getId(), cartaConsultaDestinatarios.getCartaConsulta().getId(),
				cartaConsultaDestinatarios.getOrganizacaoId(), cartaConsultaDestinatarios.getOrganizacao().getNome());
	}

}
