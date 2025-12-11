package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioRevisaoDicEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	// public EnvioRevisaoDicEmailBuilder(EnvioEmailDicDetalhesDto dto) {
	// 	super(dto);
	// }

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a) %s".formatted(dto.nomeResponsavelEnvioEmail());
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
		return "Revisão do DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {

		String corpoEmail = "O DIC do projeto [<strong>%S</strong>] requer ajustes conforme apontamentos do gestor [<strong>%S</strong>]."
				.formatted(dto.tituloProjeto(), dto.nomeGestor());

		String pontosRevisar = "<p style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\">"
				+ "<strong> Pontos a revisar: </strong>"
				+ " </p> <p style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\" > "
				+ dto.justificativaRevisao()
				+ " </p> ";

		return corpoEmail + pontosRevisar;

	}

}
