package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicador;

public record ProjetoIndicadorDto(
	Integer idIndicador,
	String tipoIndicador,
	String descricaoIndicador,
	String metaIndicador ){
		
	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getMetaIndicador()
		);
	}

}

