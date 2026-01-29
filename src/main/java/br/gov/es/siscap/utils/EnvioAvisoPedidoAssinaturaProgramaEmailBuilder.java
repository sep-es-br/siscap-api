package br.gov.es.siscap.utils;

import org.springframework.stereotype.Component;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import br.gov.es.siscap.utils.email.builder.EmailBuilderBase;

@Component
public class EnvioAvisoPedidoAssinaturaProgramaEmailBuilder extends EmailBuilderBase {

	@Override
	protected String montarCampoTratamento(EnvioEmailDicDetalhesDto dto) {
		return "Prezado(a) Gestor(a)";
	}

	@Override
	public String montarAssuntoEmail() {
		return "Assinar programa para captação.";
	}

	@Override
	protected String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) {
		return ("O Programa %s foi criado com o objetivo de prospecção de recursos.Para dar continuidade ao processo na SUBCAP, é necessária a assinatura do referido programa. <br><br> A assinatura poderá ser realizada por meio do link abaixo, em tela específica do sistema SISCAP, que realiza a integração com o e-Docs.<br>")
				.formatted(dto.nomeResponsavelEnvioEmail());
	}

	@Override
	protected String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {

		String frontEndHost = this.getEnv().getProperty("frontend.host");

		if (frontEndHost == null || frontEndHost.isBlank()) {
			return "";
		}

		String linkEdicao = frontEndHost.replaceAll("/$", "") + "/programas/editar/" + dto.idPrograma();

		if (linkEdicao == null || linkEdicao.isBlank()) {
			return "";
		}

		return """
				    <p style="font-size: 12px;" >Acesse o sistema SISCAP em: <a style="font-size: 12px;" href="%s">%s</a> </p>
				""".formatted( linkEdicao, linkEdicao );

	}

}
