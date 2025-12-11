package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioAvisoCapturaPareceresEmailBuilder extends EmailBuilderBase {
	
	String siglaProjeto;

	// public EnvioAvisoCapturaPareceresEmailBuilder(EnvioEmailDicDetalhesDto dto) {
	// 	super(dto);
	// }

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a)s";
	}

	// @Override
	// protected String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {

	// 	if (dto.linkAcessoProjeto() == null || dto.linkAcessoProjeto().isBlank()) {
	// 		return "";
	// 	}

	// 	return """
	// 			    <p>Acesse o sistema SISCAP em:</p>
	// 			    <a href="%s">%s</a>
	// 			""".formatted(dto.linkAcessoProjeto(), dto.tituloProjeto());

	// }

	@Override
	public String montarAssuntoEmail() {
		return "DIC %s disponível para análise após pareceres.".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {

		String corpoEmail = "Os pareceres Orçamentário e Estratégico do DIC já foram capturados, e o processo pode seguir para a próxima fase de análise.";

		return corpoEmail;

	}

}
