package br.gov.es.siscap.dto;

import java.time.LocalDateTime;

import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;

public record ProgramaAssinaturaEdocsDto(
	Long id,
	Long idPrograma,
    Long idPessoa,
	Integer statusAssinatura,
	LocalDateTime dataAssinatura,
	String nomeAssinante
	) {

	public ProgramaAssinaturaEdocsDto(ProgramaAssinaturaEdocs entity) {
        this(
            entity.getId(),
			entity.getPrograma().getId(),
			entity.getPessoa().getId(),
            entity.getStatusAssinatura(),
			entity.getDataAssinatura(),
			entity.getPessoa().getNome()
        );
    }

}
