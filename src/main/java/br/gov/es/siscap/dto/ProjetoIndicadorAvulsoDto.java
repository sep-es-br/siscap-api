package br.gov.es.siscap.dto;

import java.util.List;

import jakarta.validation.Valid;

public record ProjetoIndicadorAvulsoDto(
	
	Long id,

	Integer idIndicadorAvulso,

	@Valid
	IndicadorAvulsoDto indicadorAvulso,

	@Valid
	List<ProjetoIndicadorAvulsoMetaDto> metasProjeto ) {


}

