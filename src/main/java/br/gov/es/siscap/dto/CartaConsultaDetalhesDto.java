package br.gov.es.siscap.dto;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;

import java.util.List;

public record CartaConsultaDetalhesDto(

			Long id,
			ObjetoOpcoesDto objeto,
			List<OpcoesDto> projetosPropostos,
			ValorDto valor,
			String corpo
) {
}