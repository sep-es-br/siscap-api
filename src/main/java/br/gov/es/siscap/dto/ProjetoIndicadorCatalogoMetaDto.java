package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicadorExternoMeta;
import jakarta.validation.constraints.Size;

public record ProjetoIndicadorCatalogoMetaDto(
		Integer id,
		Integer anoMeta,
		@Size(max = 20, message = "O valor da meta de indicador do projeto deve possuir no máximo 20 caracteres." )
		String valorMeta) {

	public ProjetoIndicadorCatalogoMetaDto(ProjetoIndicadorExternoMeta meta) {
    this(
		meta.getId(),
		meta.getAnoMeta(),
        meta.getValorMeta()
    );

}

}