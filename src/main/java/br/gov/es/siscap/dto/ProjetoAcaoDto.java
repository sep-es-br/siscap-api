package br.gov.es.siscap.dto;

import java.math.BigDecimal;

import br.gov.es.siscap.models.ProjetoAcao;

public record ProjetoAcaoDto(
	Integer idAcao,
	String descricaoAcaoPrincipal,
	BigDecimal valorEstimadoAcaoPrincipal,
    String descricaoAcoesSecundarias ){

	public ProjetoAcaoDto(ProjetoAcao projetoAcao) {
		this(
			projetoAcao.getId(),
			projetoAcao.getDescricaoAcaoPrincipal(),
			projetoAcao.getValorEstimado(),
			projetoAcao.getDescricaoAcoesSecundarias()
		);
	}

}


