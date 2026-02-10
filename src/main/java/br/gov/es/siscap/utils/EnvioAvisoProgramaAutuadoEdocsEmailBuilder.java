package br.gov.es.siscap.utils;

import java.util.Objects;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;

@Component
public class EnvioAvisoProgramaAutuadoEdocsEmailBuilder extends EmailBuilderBase {

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Senhor(a) Subsecretário(a)<br>\r\n" + //
				"ANDRESSA RODRIGUES PAVÃO,<br>\r\n" + //
				"<br>\r\n" + //
				"Excelentíssimo(a) Senhor(a) Secretário(a)<br>\r\n" + //
				"ÁLVARO ROGÉRIO DUBOC FAJARDO,<br>\r\n" + //
				"<br>\r\n" + //
				"Excelentíssimo(a) Senhor(a) Governador(a)<br>\r\n" + //
				"JOSÉ RENATO CASAGRANDE";
	}

	@Override
	public String montarAssuntoEmail() {
		return "Programa autuado.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {
		return ("Informamos que a minuta do programa de captação de recursos %s - %s criada no Siscap - Sistema de Captação de Recursos do Estado do Espírito Santo - foi autuado com sucesso no E-Docs.")
				.formatted(Objects.toString(dto.siglaPrograma(), ""),
						Objects.toString(dto.tituloPrograma(), ""));
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {
		// String frontEndHost = this.getEnv().getProperty("frontend.host");
		// if (frontEndHost == null || frontEndHost.isBlank()) {
		// 	return "";
		// }
		// String linkEdicao = frontEndHost.replaceAll("/$", "") + "/programas/" + dto.idPrograma() + "/assinaturas";
		// if (linkEdicao == null || linkEdicao.isBlank()) {
		// 	return "";
		// }
		// return """
		// 		    <p style="font-size: 12px;" >Para acessar e autorizar, clique no link: <a style="font-size: 12px;" href="%s">%s</a> </p>
		// 		"""
		// 		.formatted(linkEdicao, linkEdicao);
		return "";
	}

}
