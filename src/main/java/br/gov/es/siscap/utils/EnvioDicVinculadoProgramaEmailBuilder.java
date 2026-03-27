package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioDicVinculadoProgramaEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezados(as)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "DIC %s vinculado a programa".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {

		return "Informamos que o DIC [<strong>%s</strong>] foi vinculado ao programa de captação [<strong>%s</strong>], passando a integrar as iniciativas elegíveis para captação de recursos."
				.formatted(this.getSiglaProjeto(), dto.siglaPrograma());

	}

}
