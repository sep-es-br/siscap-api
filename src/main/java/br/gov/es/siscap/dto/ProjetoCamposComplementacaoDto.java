package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoCamposComplementacao;

public record ProjetoCamposComplementacaoDto(
	Integer idComplemento,
	String descricaoCampo,
    String descricaoComplemento ){
		
	public ProjetoCamposComplementacaoDto(ProjetoCamposComplementacao projetoComplemento) {
		this(
			projetoComplemento.getId(),
			projetoComplemento.getCampo(),
			projetoComplemento.getMensagemComplementacao()
		);
	}

}


