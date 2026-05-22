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
	List<ProjetoIndicadorCatalogoMetaDto> metasIndicadorProjeto,
	List<ProjetoIndicadorOdsDto> odsSelecionadas ){
		
	public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
		this(
			projetoIndicador.getId(),
			projetoIndicador.getTipoIndicador(),
			projetoIndicador.getDescricaoIndicador(),
			projetoIndicador.getDescricaoMeta(),
			projetoIndicador.getTipoStatus().getId(),
			projetoIndicador.getIndicadorExterno() != null ? projetoIndicador.getIndicadorExterno().getId() : null,
			projetoIndicador.getMetas() != null 
            ? projetoIndicador.getMetas().stream()
                .map(ProjetoIndicadorCatalogoMetaDto::new)
                .toList()
            : List.of(),
			projetoIndicador.getOdsSelecionadas() != null
					? projetoIndicador.getOdsSelecionadas().stream()
						.map(ProjetoIndicadorOdsDto::new)
						.toList()
					: List.of()
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
			metasIndicadorCatalogo,
			projetoIndicador.getOdsSelecionadas() != null
					? projetoIndicador.getOdsSelecionadas().stream()
						.map(ProjetoIndicadorOdsDto::new)
						.toList()
					: List.of()
		);
	}

}

