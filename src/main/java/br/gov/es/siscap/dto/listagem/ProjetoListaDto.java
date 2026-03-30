package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.StatusProjeto;
import java.math.BigDecimal;
import java.util.Optional;

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
	public ProjetoListaDto( Projeto projeto, BigDecimal valorEstimado, Long lotacaoUsuario ) {
		this(
			projeto.getId(),
			projeto.getSigla(),
			projeto.getTitulo(),
                        Optional.ofNullable(projeto.getStatusAtual()).map(StatusProjeto::getStatus).orElse(null),
			valorEstimado,
			projeto.isRascunho(),
			projeto.getProtocoloEdocs(),
			lotacaoUsuario
		);
	}
}