package br.gov.es.siscap.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDetalhesDto;
import br.gov.es.siscap.dto.ProjetoCamposComplementacaoDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EnvioComplementoDicEmailBuilder extends EmailBuilderBase {

	String siglaProjeto;

	// List<ProjetoCamposComplementacaoDto> complementacaoDtos;

	@Override
	protected String montarCampoTratamento(EnvioEmailDetalhesDto dto) {
		return "Prezado(a) %s".formatted(dto.nomeGestor());
	}

	@Override
	public String montarAssuntoEmail() {
		return "Complementação do DIC %s".formatted(this.getSiglaProjeto());
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDetalhesDto dto) {

		String corpoEmail = "Informamos que o DIC [<strong>%S</strong>] precisará ser complementado após avaliação realizada pela SUBCAP."
				.formatted(dto.tituloProjeto())
				+ "<p style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\" > <strong> Complementações a serem realizadas: </strong>"
				+ gerarListaComplementosOrdenadaHtml(dto.camposSeremComplementados()) + " </p> ";
		;

		return corpoEmail;

	}

	private static String gerarListaComplementosOrdenadaHtml(
			List<ProjetoCamposComplementacaoDto> complementacoes) {

		StringBuilder html = new StringBuilder("<ol style=\"font-size: 12px; color: #000000; margin-bottom: 5px;\">");

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
