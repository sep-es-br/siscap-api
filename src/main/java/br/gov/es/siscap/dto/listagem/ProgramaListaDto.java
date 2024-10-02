package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.models.Programa;

import java.math.BigDecimal;

public record ProgramaListaDto(

			Long id,
			String sigla,
			String titulo,
			String moeda,
			BigDecimal valor
) {

	public ProgramaListaDto(Programa programa, ValorDto valorDto) {
		this(
					programa.getId(),
					programa.getSigla(),
					programa.getTitulo(),
					valorDto.moeda(),
					valorDto.quantia()
		);
	}
}
