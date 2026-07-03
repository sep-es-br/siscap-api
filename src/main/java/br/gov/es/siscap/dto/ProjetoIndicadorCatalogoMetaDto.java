package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;

public record ProjetoIndicadorCatalogoMetaDto(
		Integer id,
		Integer anoMeta,
		String valorMeta) {

	public ProjetoIndicadorCatalogoMetaDto(ProjetoIndicadorExternoMeta meta) {
    this(
		meta.getId(),
		meta.getAnoMeta(),
        meta.getValorMeta()
    );

}

}