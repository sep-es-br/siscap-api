package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;

public record ProjetoListaDto(

			Long id,
			String sigla,
			String titulo,
			String status,
			BigDecimal valorEstimado,
			boolean isRascunho,
			String protocoloEdocs,
			Long lotacaoUsuario
) {
	public ProjetoListaDto(Projeto projeto, BigDecimal valorEstimado, Long lotacaoUsuario) {
		this(
			projeto.getId(),
			projeto.getSigla(),
			projeto.getTitulo(),
			projeto.getStatus(),
			valorEstimado,
			projeto.isRascunho(),
			projeto.getProtocoloEdocs(),
			lotacaoUsuario
		);
	}
}