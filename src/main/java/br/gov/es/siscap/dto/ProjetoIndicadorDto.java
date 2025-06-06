package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicador;
import br.gov.es.siscap.models.TipoStatus;

public record ProjetoIndicadorDto(
	Integer idIndicador,
	String tipoIndicador,
	String descricaoIndicador,
	String descricaoMeta,
	Long idStatus ){
		
	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getDescricaoMeta(),
			projetoIndicador.getTipoStatus().getId()
		);
	}

}

