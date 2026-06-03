package br.gov.es.siscap.dto;

import java.util.List;

import br.gov.es.siscap.models.ProjetoIndicadorAvulso;
import jakarta.validation.Valid;

public record ProjetoIndicadorAvulsoDto(

	Integer id,

	Integer idIndicadorAvulso,

	@Valid
	IndicadorAvulsoDto indicadorAvulso,
	
	@Valid	List<ProjetoIndicadorAvulsoMetaDto> metasIndicadorProjeto ) {

	public ProjetoIndicadorAvulsoDto(ProjetoIndicadorAvulso projetoIndicadorAvulso) {
		this(projetoIndicadorAvulso.getId(),
				projetoIndicadorAvulso.getIndicadorAvulso().getId(),
				new IndicadorAvulsoDto(projetoIndicadorAvulso.getIndicadorAvulso()),
				projetoIndicadorAvulso.getMetas().stream().map(ProjetoIndicadorAvulsoMetaDto::new).toList());

	}

}
