package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.IndicadorAvulsoMeta;

public record ProjetoIndicadorAvulsoMetaDto(
		Integer id,
		Integer anoMeta,
		String valorMeta) {

	public ProjetoIndicadorAvulsoMetaDto(IndicadorAvulsoMeta meta) {
		this(
				meta.getId(),
				meta.getAno(),
				meta.getValor());

	}

}