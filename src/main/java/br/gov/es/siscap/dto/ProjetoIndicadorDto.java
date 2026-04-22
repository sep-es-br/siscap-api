package br.gov.es.siscap.dto;

import java.util.List;

import br.gov.es.siscap.models.ProjetoIndicador;

public record ProjetoIndicadorDto(
	
	Integer idIndicador,
	String tipoIndicador,
	String descricaoIndicador,
	String descricaoMeta,
	Long idStatus,
	Integer idIndicadorExterno,
	List<ProjetoIndicadorCatalogoMetaDto> metasProjetoIndicadorExterno){
		
	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getDescricaoMeta(),
			projetoIndicador.getTipoStatus().getId(),
			projetoIndicador.getIndicadorExterno().getId(),
			null
		);
	}

	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador, List<ProjetoIndicadorCatalogoMetaDto> metasIndicadorCatalogo) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getDescricaoMeta(),
			projetoIndicador.getTipoStatus().getId(),
			projetoIndicador.getIndicadorExterno().getId(),
			metasIndicadorCatalogo
		);
	}

}

