package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Programa;

import java.math.BigDecimal;

public record ProgramaListaDto(

			Long id,
			String sigla,
			String titulo,
			String moeda,
			BigDecimal tetoPrograma,
			String protocoloEDocs
) {

	public ProgramaListaDto(Programa programa) {
		this(
					programa.getId(),
					programa.getSigla(),
					programa.getTitulo(),
					programa.getMoeda(),
					programa.getTetoQuantia(),
                    programa.getProtocoloEdocs()
		);
	}
}