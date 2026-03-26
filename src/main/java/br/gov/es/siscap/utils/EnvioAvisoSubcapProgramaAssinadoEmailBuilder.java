package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioAvisoSubcapProgramaAssinadoEmailBuilder extends EmailBuilderBase {

	String siglaPrograma;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezado(a)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "Programa %s assinado.".formatted(this.getSiglaPrograma());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		return "Informamos que o programa <strong>%s</strong> teve seu fluxo de assinaturas concluído, estando devidamente firmado por todas as partes competentes.".formatted(dto.tituloPrograma());
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDetalhesDto dto) {
		return "";
	}

}
