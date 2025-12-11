package br.gov.es.siscap.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioComplementoDicEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;
	List<ProjetoCamposComplementacaoDto> complementacaoDtos;

	// public EnvioComplementoDicEmailBuilder(EnvioEmailDicDetalhesDto dto) {
	// 	super(dto);
	// }

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a) %s".formatted(dto.nomeGestor());
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
		return "Complementação do DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {

		String corpoEmail = "Informamos que o DIC [<strong>%S</strong>] precisará ser complementado após avaliação realizada pela SUBCAP."
				.formatted(dto.tituloProjeto()) + "<p style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\" > <strong> Complementações a serem realizadas: </strong>"
				+ " </p> <br> <p style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\" > "
				+ gerarListaComplementosOrdenadaHtml(this.getComplementacaoDtos()) + " </p> ";
		;

		return corpoEmail;

	}

	private static String gerarListaComplementosOrdenadaHtml(
			List<ProjetoCamposComplementacaoDto> complementacoes) {

		StringBuilder html = new StringBuilder("<ol>");

		complementacoes.stream()
				.forEach(complemento -> html.append("<li>")
						.append("<strong>")
						.append(complemento.descricaoCampo())
						.append(":</strong> ")
						.append(complemento.descricaoComplemento())
						.append("</li>"));

		html.append("</ol>");

		return html.toString();
	}

}
