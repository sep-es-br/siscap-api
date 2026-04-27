package br.gov.es.siscap.dto;

import jakarta.validation.constraints.NotNull;

public record ProjetoIndicadorCatalogoMetaDto(

		Long idLocalidade,

		String ano,

		String valor

) {
}