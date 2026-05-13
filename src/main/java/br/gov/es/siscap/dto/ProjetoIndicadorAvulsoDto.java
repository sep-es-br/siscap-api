package br.gov.es.siscap.dto;

import java.util.List;

import br.gov.es.siscap.models.ProjetoIndicador;

public record ProjetoIndicadorAvulsoDto(
	
	Integer idIndicador,
	String medidoPor,
	String nomeIndicador,
	String unidadeMedida,
	String baseDeReferencia,
	String fonteIndicador,
	List<ProjetoIndicadorAvulsoMetaDto> metasIndicadorProjeto,
	List<ProjetoIndicadorAvulsoMetaDto> metasIndicadorAvulsoProjeto){
		
	// public ProjetoIndicadorAvulsoDto(ProjetoIndicador projetoIndicador) {
	// 	this(
	// 		projetoIndicador.getId(),
	// 		projetoIndicador.getTipoIndicador(),
	// 		projetoIndicador.getDescricaoIndicador(),
	// 		projetoIndicador.getDescricaoMeta(),
	// 		projetoIndicador.getTipoStatus().getId(),
	// 		projetoIndicador.getIndicadorExterno() != null ? projetoIndicador.getIndicadorExterno().getId() : null,
	// 		projetoIndicador.getMetas() != null 
    //         ? projetoIndicador.getMetas().stream()
    //             .map(ProjetoIndicadorCatalogoMetaDto::new)
    //             .toList()
    //         : List.of()
	// 	);
	// }

}

