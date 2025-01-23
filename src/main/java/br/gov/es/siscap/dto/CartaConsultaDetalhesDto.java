package br.gov.es.siscap.dto;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.models.CartaConsulta;

import java.util.List;

public record CartaConsultaDetalhesDto(

			Long id,
			String codigoCartaConsulta,
			ObjetoOpcoesDto objeto,
			List<OpcoesDto> projetosPropostos,
			ValorDto valor,
			String corpo,
			boolean prospectado
) {

	public CartaConsultaDetalhesDto(CartaConsulta cartaConsulta, List<OpcoesDto> projetosPropostos, ValorDto valor, String corpo) {
		this(
					cartaConsulta.getId(),
					cartaConsulta.gerarCodigoCartaConsulta(),
					cartaConsulta.getCartaConsultaObjeto(),
					projetosPropostos,
					valor,
					corpo,
					cartaConsulta.isProspectado()
		);
	}
}