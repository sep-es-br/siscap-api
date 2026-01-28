package br.gov.es.siscap.dto;

import java.time.LocalDateTime;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;

public record ProgramaAssinaturaEdocsDto(
	Long id,
	Long idPrograma,
    Pessoa pessoa,
	Integer statusAssinatura,
	LocalDateTime dataAssinatura
	) {

	public ProgramaAssinaturaEdocsDto(ProgramaAssinaturaEdocs entity) {
        this(
            entity.getId(),
			entity.getPrograma().getId(),
			entity.getPessoa(),
            entity.getStatusAssinatura(),
			entity.getDataAssinatura()
        );
    }

}
