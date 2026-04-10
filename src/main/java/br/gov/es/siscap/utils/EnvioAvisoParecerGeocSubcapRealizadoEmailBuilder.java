package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezado(a)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "Parecer gerencial de captação DIC %s realizado".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		return "Comunicamos que foi realizado o parecer GEOC para o <strong>DIC (Documento Inicial para Captação)</strong>.";
	}
	
}
