package br.gov.es.siscap.utils.email.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class EmailBuilderBase implements EmailBuilder {


    private EnvioEmailDicDetalhesDto dtoMontagemEmailDic = null;

    @Autowired
    private Environment env;

    @Override
    public final String montarCorpoEmail() {

        String cabecalhoSuperior =
                "<tr>" +
                    "<td style=\"padding: 20px; background-color: #7eb4f2; width: 100%;\">" +
                        "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
                            "<tr>" +
                                "<td style=\"vertical-align: middle;\">" +
                                    "<img src=\"cid:govES-logo\" style=\"height: 60px; vertical-align: middle; margin-right: 10px;\">" +
                                "</td>" +
                                "<td style=\"vertical-align: middle; text-align: right;\">" +
                                    "<p style=\"margin: 0; color: #ffffff; font-size: 16px;\">" +
                                        "Captação de Recursos - Governo do Estado do Espírito Santo" +
                                    "</p>" +
                                "</td>" +
                            "</tr>" +
                        "</table>" +
                    "</td>" +
                "</tr>";

        String rodapeEmail =
                "<tr>" +
                    "<td style=\"padding: 20px; background-color: #7eb4f2; width: 100%;\">" +
                        "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
                            "<tr>" +
                                "<td style=\"vertical-align: middle;\">" +
                                    "<img src=\"cid:icon-siscap-white\" style=\"height: 30px; vertical-align: middle; margin-right: 10px;\">" +
                                "</td>" +
                                "<td style=\"vertical-align: middle; text-align: right;\">" +
                                    "<p style=\"margin: 0; color: #ffffff; font-size: 14px;\">" +
                                        "Desenvolvido e mantido pela SEP - Secretaria de Economia e Planejamento." +
                                    "</p>" +
                                "</td>" +
                            "</tr>" +
                        "</table>" +
                    "</td>" +
                "</tr>";

        String campoTratamento = montarCampoTratamento(dtoMontagemEmailDic);
        String campoCorpo = montarCorpoPrincipal(dtoMontagemEmailDic);
        String linkAcesso = montarLinkAcesso(dtoMontagemEmailDic);

        return montarHtmlTemplate(cabecalhoSuperior, campoTratamento, campoCorpo, linkAcesso, rodapeEmail);

    }

    private String montarHtmlTemplate(String cabecalhoSuperior,
            String campoTratamento,
            String campoCorpoPrincipal,
            String linkAcessoDic,
            String rodapeEmail) {

        String html = "<html>" +
					"<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">" +
                    """
                    <table width="100%" cellspacing="0" cellpadding="0" style="background-color: #f5f5f5;">
                      <tr>
                        <td align="center" style="padding: 20px 0;">
                          <table width="800" cellspacing="0" cellpadding="0" style="background-color: #ffffff;">
                            {{CABECALHO}}
                            <tr><td style="padding: 0 12px;"><hr style="border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;"></td></tr>
                            <tr><td style="padding: 15px 20px; background-color: #fcea8f; font-size: 12;">
                              <strong style="color: #d32f2f;">Atenção:</strong> Este é um e-mail automático. Favor não responder.</td>
                            </tr>
                            <tr><td style="padding: 0 20px;"><hr style="border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;"></td></tr>
                            <tr><td style="padding: 0 20px 20px 20px;">
                              <p style="font-size: 12px;">{{TRAT}},</p>
                              <p style="font-size: 12px;">{{CORPO}}</p>
                              {{LINK}}
                            </td></tr
                            <tr><td style="padding: 0 20px 20px 20px;"><p style="font-size: 12;">Em caso de dúvidas, estamos à disposição para prestar o suporte necessário.</p></td></tr
                            {{RODAPE}}
                          </table>
                        </td>
                      </tr>
                    </table>
                    """ + "</body>" + "</html>" ;

        html = html.replace("{{CABECALHO}}",cabecalhoSuperior)
                .replace("{{TRAT}}", campoTratamento)
                .replace("{{CORPO}}", campoCorpoPrincipal)
                .replace("{{LINK}}", linkAcessoDic)
                .replace("{{RODAPE}}", rodapeEmail);

        return html;

    }

    protected abstract String montarCampoTratamento(EnvioEmailDicDetalhesDto dto);

    protected abstract String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) ;

    private String montarLinkAcesso(EnvioEmailDicDetalhesDto dto) {

        String frontEndHost = env.getProperty("frontend.host");

        if (frontEndHost == null || frontEndHost.isBlank()) {
            return "";
		}

        String linkEdicao = frontEndHost.replaceAll("/$", "") + "/projetos/editar/" + dto.idProjeto();

		if (linkEdicao == null || linkEdicao.isBlank()) {
			return "";
		}

		return """
				    <p style="font-size: 12px;" >Acesse o sistema SISCAP em:</p>
				    <a style="font-size: 12px;" href="%s">%s</a>
				""".formatted( linkEdicao, "Clique aqui para acessar" );

	}

}
