package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoCamposComplementacao;

public record ProjetoCamposComplementacaoDto(
	Integer idComplemento,
	String idCampo,
	String descricaoCampo,
    String descricaoComplemento ){
		
	public ProjetoCamposComplementacaoDto(ProjetoCamposComplementacao projetoComplemento, String descricaoCampo) {
		this(
			projetoComplemento.getId(),
			projetoComplemento.getCampo(),
			descricaoCampo,
			projetoComplemento.getMensagemComplementacao()
		);
	}

}


