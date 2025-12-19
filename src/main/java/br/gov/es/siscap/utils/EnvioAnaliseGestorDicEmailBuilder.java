package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;

@Component
public class EnvioAnaliseGestorDicEmailBuilder extends EmailBuilderBase {

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a) Gestor(a)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "DIC disponível para análise e autuação via E-Docs.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {
		return ("O(A) servidor(a) %s elaborou um DIC (Documento Inicial para Captação de Recursos) disponível para análise e tramitação diretamente no SISCAP.<br><br>")
				.formatted(dto.nomeResponsavelEnvioEmail());
	}

}
