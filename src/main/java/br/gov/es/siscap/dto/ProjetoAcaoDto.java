package br.gov.es.siscap.dto;

import java.math.BigDecimal;

import br.gov.es.siscap.models.ProjetoAcao;

public record ProjetoAcaoDto(
	Integer idAcao,
	String acaoPrincipal,
	BigDecimal valorEstimado,
    String descricaoAcoesSecundarias ){
		
	public ProjetoAcaoDto(ProjetoAcao projetoAcao) {
		this(
			projetoIndicador.getIdAcao(),
			projetoIndicador.getDescricaoA(),
			projetoIndicador.getMetaIndicador()
		);
	}

}


