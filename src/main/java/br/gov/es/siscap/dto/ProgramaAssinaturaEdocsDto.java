package br.gov.es.siscap.dto;

import java.time.LocalDateTime;

import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Programa;
import br.gov.es.siscap.models.ProgramaAssinaturaEdocs;

public record ProgramaAssinaturaEdocsDto(Long id,
	Programa programa,
    Pessoa pessoa,
	Integer statusAssinatura,
	LocalDateTime dataAssinatura
	) {

	public ProgramaAssinaturaEdocsDto(ProgramaAssinaturaEdocs entity) {
        this(
            entity.getId(),
			entity.getPrograma(),
			entity.getPessoa(),
            entity.getStatusAssinatura(),
			entity.getDataAssinatura()
        );
    }

}
