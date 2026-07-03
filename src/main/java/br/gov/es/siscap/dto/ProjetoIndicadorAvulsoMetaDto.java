package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.IndicadorAvulsoMeta;
import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;

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

	// The type ProjetoIndicadorAvulsoMetaDto does not define ProjetoIndicadorAvulsoMetaDto(ProjetoIndicadorAvulso) 
	// that is applicable hereJava(603979903)
	public ProjetoIndicadorAvulsoMetaDto(ProjetoIndicadorAvulsoMeta projetoIndicadorAvulsoMeta) {
		this(
			projetoIndicadorAvulsoMeta.getId(),
			projetoIndicadorAvulsoMeta.getAno(),
			projetoIndicadorAvulsoMeta.getValor()
		);
	}
}