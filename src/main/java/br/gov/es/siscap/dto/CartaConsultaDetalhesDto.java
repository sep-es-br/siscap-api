package br.gov.es.siscap.dto;

import java.util.List;

public record CartaConsultaDetalhesDto(
			Long id,
			ObjetoSelectDto objeto,
			List<SelectDto> projetosPropostos,
			ValorDto valor,
			String corpo
) {
}
