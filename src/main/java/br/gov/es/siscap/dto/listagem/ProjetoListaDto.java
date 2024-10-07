package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;
import java.util.List;

public record ProjetoListaDto(

			Long id,
			String sigla,
			String titulo,
			String moeda,
			BigDecimal valor,
			List<String> nomesMicrorregioesRateio
) {
	public ProjetoListaDto(Projeto projeto, ValorDto valorDto, List<String> nomesMicrorregioesRateio) {
		this(
					projeto.getId(),
					projeto.getSigla(),
					projeto.getTitulo(),
					valorDto.moeda(),
					valorDto.quantia(),
					nomesMicrorregioesRateio
		);
	}
}
