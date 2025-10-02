package br.gov.es.siscap.utils;

import java.util.List;
import java.util.Map;

public abstract class EnvioComplementoDicEmailBuilder {

	private static final String CAMPO_APRESENTACAO = "Prezado(a) %s";
	private static final String CAMPO_CORPO = "Informamos que o projeto [<strong>%S</strong>] precisará ser complementado após avaliação realizada pela SUBCAP.";
	private static final String CAMPO_TITULO_COMPLEMENTACAO = "<strong> Complementações a serem realizadas: </strong>";
		
	public static String montarAssuntoEmail(String descricaoProjeto) {
		return "Complementação do DIC " + descricaoProjeto + ".";
	}
	
	public static String montarCorpoEmail( String nomeResponsavelEnvioEmail, String descricaoProjeto, String nomeGestorResponsavel, Map<String, String> complemetacoes ) {
		String campoTable = montarElementoTable( nomeResponsavelEnvioEmail, complemetacoes, descricaoProjeto, nomeGestorResponsavel );
		String corpoEmail = "<html>" +
			"<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">" +
			campoTable +
			"</body>" +
			"</html>";
		return corpoEmail;
	}

	private static String montarElementoTable(String nomeResponsavelEnvioEmail, Map<String, String> complemetacoes, 
		String descricaoProjeto, String nomeGestorResponsavel) {

		String cabecalhoSuperior = "<td style=\"padding: 20px; background-color: #2a8fbd; width: 100%;\"> <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> <tr> <td style=\"vertical-align: middle;\"> <img src=\"cid:govES-logo\" style=\"height: 30px; vertical-align: middle; margin-right: 10px;\"> </td><td style=\"vertical-align: middle;\"><h1 style=\"margin: 0; color: #ffffff; font-size: 20px; font-family: Arial, sans-serif;\">Sistema de Captação - Governo do Estado do Espírito Santo</h1></td></tr></table></td>";
		String campoTratamento = String.format(CAMPO_APRESENTACAO, nomeResponsavelEnvioEmail);
		String campoCorpoPrincipal = String.format(CAMPO_CORPO, descricaoProjeto, nomeGestorResponsavel ) ;
		String pontosSeremComplementados = "<p style=\"font-size: 14px; color: #000000; margin-bottom: 5px;\">" + CAMPO_TITULO_COMPLEMENTACAO + " </p> <br> <p style=\"font-size: 14px; color: #000000; margin-bottom: 5px;\" > " + gerarListaComplementosOrdenadaHtml(complemetacoes)  + " </p> "; 

		return "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: #f5f5f5;\">  <tr>  <td align=\"center\" style=\"padding: 20px 0;\">  <table width=\"800\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: #ffffff; border-radius: 4px; box-shadow: 0 2px 4px #ffffff; max-width: 100%;\"> " + cabecalhoSuperior + " <tr><td style=\"padding: 0 12px;\"><hr style=\"border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;\"></td></tr> <tr> <td style=\"padding: 15px 20px; width:90%; background-color: #fcea8f; font-size: 14px;\"><strong style=\"color: #d32f2f;\">Atenção:</strong> Este é um e-mail automático. Favor não responder.</td></tr>   <tr><td style=\"padding: 0 20px;\"><hr style=\"border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;\"></td></tr> <tr><td style=\"padding: 0 20px 20px 20px;\"> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\"> " + campoTratamento + ", </p> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\"> " + campoCorpoPrincipal + "</p> " + pontosSeremComplementados + " </td> </tr> <td style=\"padding: 0 20px 20px 20px;\"> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\">Em caso de dúvidas, estamos à disposição para prestar o suporte necessário.</p> </td> <tr><td style=\"padding: 15px 20px; background-color: #2a8fbd; text-align: center; font-size: 12px; color: #ffffff;\">©2025 - Desenvolvido pela CGTI-SEP <img src=\"cid:Icon-siscap\" style=\"height: 15px; vertical-align: middle; margin-right: 10px;\"> </td> </tr> </table> </td> </tr> </table>" ;
	
	}

	private static String gerarListaComplementosOrdenadaHtml( Map<String, String> complementacoes ) {
		
		StringBuilder html = new StringBuilder("<ol>");
		
		complementacoes.entrySet().stream()
			.forEach(entry -> html.append("<li>")
								.append("<strong>").append(entry.getKey()).append(":</strong> ")
								.append(entry.getValue())
								.append("</li>"));

		html.append("</ol>");

		return html.toString();

	}
	
}
