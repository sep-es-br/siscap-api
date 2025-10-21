package br.gov.es.siscap.dto;

import java.time.LocalDateTime;

import br.gov.es.siscap.enums.StatusParecerEnum;
import br.gov.es.siscap.models.ProjetoParecer;

public record ProjetoParecerDto(
		Long id,
		Long idProjeto,
		String guidUnidadeOrganizacao,
		String textoParecer,
		StatusParecerEnum statusParecer,
		LocalDateTime dataEnvio,
		String guidDocumentoEdocs) {

	public ProjetoParecerDto(ProjetoParecer projetoParecer) {
		this(
				projetoParecer.getId(),
				projetoParecer.getProjeto().getId(),
				projetoParecer.getGuidUnidadeOrganizacao(),
				projetoParecer.getTextoParecer(),
				projetoParecer.getStatusParecer(),
				projetoParecer.getDataEnvio(),
				projetoParecer.getGuidDocumentoEdocs());
	}

}
