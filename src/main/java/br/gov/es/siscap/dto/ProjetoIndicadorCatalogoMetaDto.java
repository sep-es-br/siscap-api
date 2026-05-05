package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;

public record ProjetoIndicadorCatalogoMetaDto(
		Integer idFato,
		Integer anoMeta,
		String valorMeta) {

	public ProjetoIndicadorCatalogoMetaDto(ProjetoIndicadorExternoMeta meta) {
    this(
        meta.getIdFato(),
		meta.getAnoMeta(),
        meta.getValorMeta()
    );

}

}