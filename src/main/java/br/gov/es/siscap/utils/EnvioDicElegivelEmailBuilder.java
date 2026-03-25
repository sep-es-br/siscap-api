package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioDicElegivelEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezados(as)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "DIC %s - ELEGÍVEL".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {

		return "Informamos que o DIC [<strong>%s</strong>] está elegível para ser vinculado a um programa de captação."
				.formatted(dto.tituloProjeto());

	}

}
