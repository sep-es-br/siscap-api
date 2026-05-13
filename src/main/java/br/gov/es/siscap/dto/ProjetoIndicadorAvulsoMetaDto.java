package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.IndicadorAvulsoMeta;

public record ProjetoIndicadorAvulsoMetaDto(
		Integer anoMeta,
		String valorMeta) {

	public ProjetoIndicadorAvulsoMetaDto( IndicadorAvulsoMeta meta ) {
    this(
		meta.getAno(),
        meta.getValor()
    );

}

}