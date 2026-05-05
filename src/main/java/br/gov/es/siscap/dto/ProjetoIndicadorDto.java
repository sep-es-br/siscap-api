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
	List<ProjetoIndicadorCatalogoMetaDto> metas){
		
	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getDescricaoMeta(),
			projetoIndicador.getTipoStatus().getId(),
			projetoIndicador.getIndicadorExterno() != null ? projetoIndicador.getIndicadorExterno().getId()  : null,
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
			projetoIndicador.getIndicadorExterno() != null ? projetoIndicador.getIndicadorExterno().getId()  : null,
			metasIndicadorCatalogo
		);
	}

}

