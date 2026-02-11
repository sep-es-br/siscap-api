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

	// public EnvioAvisoParecerGeocSubcapRealizadoEmailBuilder(EnvioEmailDicDetalhesDto dto) {
	// 	super(dto);
	// }

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezado(a) Gestor(a) da <strong>GEOC - Gerência de Operação de Crédito</strong>";
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
		return "Pedido parecer gerencial SUBCAP DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		
		String corpoEmail = "Comunicamos que há um <strong>DIC (Documento Inicial para Captação)</strong> disponível para <strong>emissão de parecer</strong>.";

		return corpoEmail;

	}
	
}
