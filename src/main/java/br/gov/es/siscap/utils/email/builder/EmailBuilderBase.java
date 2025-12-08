package br.gov.es.siscap.utils.email.builder;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;

public abstract class EmailBuilderBase implements EmailBuilder {

    private EnvioEmailDicDetalhesDto dtoMontagemEmailDic = null;

    protected EmailBuilderBase(EnvioEmailDicDetalhesDto dto) {
        this.dtoMontagemEmailDic = dto;
    }

    @Override
    public final String montarCorpoEmail() {

        String cabecalhoSuperior = "<td style=\"padding: 20px; background-color: #7eb4f2; width: 100%;\">" +
                "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
                "<tr>" +
                "<td style=\"vertical-align: middle;\">" +
                "<img src=\"cid:govES-logo\" style=\"height: 60px; vertical-align: middle; margin-right: 10px;\">" +
                "</td>" +
                "<td style=\"vertical-align: right;\">" +
                "<h1 style=\"vertical-align: right; margin: 0; text-align: right; color: #ffffff; font-size: 20px;\">" +
                "Captação de Recursos - Governo do Estado do Espírito Santo" +
                "</h1>" +
                "</td>" +
                "</tr></table></td>";

        String rodapeEmail =  "<td style=\"padding: 20px; background-color: #7eb4f2; width: 100%;\">" +
                "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
                "<tr>" +
                "<td style=\"vertical-align: middle;\">" +
                "<img src=\"cid:Icon-siscap\" style=\"height: 60px; vertical-align: middle; margin-right: 10px;\">" +
                "</td>" +
                "<td style=\"vertical-align: right;\">" +
                "<h1 style=\"vertical-align: right; margin: 0; text-align: right; color: #ffffff; font-size: 20px;\">" +
                "Desenvolvido e mantido pela SEP - Secretaria de Economia e Planejamento." +
                "</h1>" +
                "</td>" +
                "</tr></table></td>";

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

        String html = """
                    <table width="100%" cellspacing="0" cellpadding="0" style="background-color: #f5f5f5;">
                      <tr>
                        <td align="center" style="padding: 20px 0;">
                          <table width="800" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 4px;">
                            {{CABECALHO}}
                            <tr><td style="padding: 0 12px;"><hr style="border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;"></td></tr>
                            <tr><td style="padding: 15px 20px; background-color: #fcea8f; font-size: 14px;">
                              <strong style="color: #d32f2f;">Atenção:</strong> Este é um e-mail automático. Favor não responder.</td>
                            </tr>
                            <tr><td style="padding: 0 20px;"><hr style="border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;"></td></tr>
                            <tr><td style="padding: 0 20px 20px 20px;">
                              <p style="font-size: 14px;">{{TRAT}},</p>
                              <p style="font-size: 14px;">{{CORPO}}</p>
                              {{LINK}}
                            </td></tr
                            {{RODAPE}}
                          </table>
                        </td>
                      </tr>
                    </table>
                """;

        html = html.replace("{{CABECALHO}}",cabecalhoSuperior)
                .replace("{{TRAT}}", campoTratamento)
                .replace("{{CORPO}}", campoCorpoPrincipal)
                .replace("{{LINK}}", linkAcessoDic)
                .replace("{{RODAPE}}", rodapeEmail);

        return html;

    }

    protected abstract String montarCampoTratamento(EnvioEmailDicDetalhesDto dto);

    protected abstract String montarCorpoPrincipal(EnvioEmailDicDetalhesDto dto) ;

    protected abstract String montarLinkAcesso(EnvioEmailDicDetalhesDto dto);

}
