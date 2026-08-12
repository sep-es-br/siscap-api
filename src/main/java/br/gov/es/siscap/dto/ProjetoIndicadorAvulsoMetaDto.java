package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.IndicadorAvulsoMeta;
import br.gov.es.siscap.models.ProjetoIndicadorAvulsoMeta;
import jakarta.validation.constraints.Size;

public record ProjetoIndicadorAvulsoMetaDto(
		Integer id,
		Integer anoMeta,
		@Size(max = 20, message = "O valor da meta do indicador de projeto deve possuir no máximo 20 caracteres." )
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