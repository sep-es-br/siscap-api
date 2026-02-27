package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;

@Component
public class EnvioAnaliseGestorDicEmailBuilder extends EmailBuilderBase {

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezado(a) Gestor(a)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "DIC disponível para análise e autuação.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {
		return ("O(A) servidor(a) %s elaborou um DIC (Documento Inicial para Captação) de Recursos, que está disponível para a sua análise e tramitação diretamente no SISCAP")
				.formatted(dto.nomeResponsavelEnvioEmail());
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDetalhesDto dto) {

		String frontEndHost = this.getEnv().getProperty("frontend.host");

		if (frontEndHost == null || frontEndHost.isBlank()) {
			return "";
		}

		String linkEdicao = frontEndHost.replaceAll("/$", "") + "/main/projetos/editar/" + dto.idProjeto();

		if (linkEdicao == null || linkEdicao.isBlank()) {
			return "";
		}

		return """
					em, <a style="font-size: 12px;" href="%s">%s</a>
				"""
				.formatted(linkEdicao, linkEdicao);

	}

}
