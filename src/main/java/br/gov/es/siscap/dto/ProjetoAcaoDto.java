package br.gov.es.siscap.dto;

import java.math.BigDecimal;

import br.gov.es.siscap.models.ProjetoAcao;

public record ProjetoAcaoDto(
	Integer idAcao,
	String descricaoAcaoPrincipal,
	BigDecimal valorEstimadoAcaoPrincipal,
    String descricaoAcaoSecundaria,
	Long idStatus ){

	public ProjetoAcaoDto(ProjetoAcao projetoAcao) {
		this(
			projetoAcao.getId(),
			projetoAcao.getDescricaoAcaoPrincipal(),
			projetoAcao.getValorEstimado(),
			projetoAcao.getDescricaoAcaoSecundaria(),
			projetoAcao.getProjeto().getTipoStatus().getId()
		);
	}

}


