package br.gov.es.siscap.dto;

import java.time.LocalDateTime;

import br.gov.es.siscap.models.ProjetoParecer;

public record ProjetoParecerDto(
		Long id,
		Long idProjeto,
		String guidUnidadeOrganizacao,
		String textoParecer,
		Long statusParecer,
		LocalDateTime dataEnvio,
		String guidDocumentoEdocs,
		String usuarioFezEnvioParecer,
		Long parecerLotacao,
		String registroArquivoEdocs) {

	public ProjetoParecerDto(ProjetoParecer projetoParecer, String usuarioFezEnvioParecer) {
		this(
				projetoParecer.getId(),
				projetoParecer.getProjeto().getId(),
				projetoParecer.getGuidUnidadeOrganizacao(),
				projetoParecer.getTextoParecer(),
				projetoParecer.getStatusParecer(),
				projetoParecer.getDataEnvio(),
				projetoParecer.getGuidDocumentoEdocs(),
				usuarioFezEnvioParecer,
				projetoParecer.getLotacaoParecer().getValue(),
				projetoParecer.getRegistroArquivoEdocs());
	}

	public ProjetoParecerDto(ProjetoParecer projetoParecer) {
		this(
				projetoParecer.getId(),
				projetoParecer.getProjeto().getId(),
				projetoParecer.getGuidUnidadeOrganizacao(),
				projetoParecer.getTextoParecer(),
				projetoParecer.getStatusParecer(),
				projetoParecer.getDataEnvio(),
				projetoParecer.getGuidDocumentoEdocs(),
				projetoParecer.getSubUsuarioEnviou(),
				projetoParecer.getLotacaoParecer().getValue(),
				projetoParecer.getRegistroArquivoEdocs());
	}

}
