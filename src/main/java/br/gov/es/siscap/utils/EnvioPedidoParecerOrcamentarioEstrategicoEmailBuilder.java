package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;

public abstract class EnvioPedidoParecerOrcamentarioEstrategicoEmailBuilder {
	
	public static String montarAssuntoEmail() {
		return "Pedido de parecer ";
	}

	public static String montarCorpoEmail(EnvioEmailDicDetalhesDto envioDetalhesDto) {
				
		String campoTable = montarElementoTable(envioDetalhesDto);
		
		String corpoEmail = "<html>" +
					"<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">" +
					campoTable +
					"</body>" +
					"</html>";
	
		return corpoEmail;

	}

	private static String montarElementoTable(EnvioEmailDicDetalhesDto envioDetalhesDto) {
		
		String cabecalhoSuperior = "<td style=\"padding: 20px; background-color: #2a8fbd; width: 100%;\"> <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> <tr> <td style=\"vertical-align: middle;\"> <img src=\"cid:govES-logo\" style=\"height: 30px; vertical-align: middle; margin-right: 10px;\"> </td><td style=\"vertical-align: middle;\"><h1 style=\"margin: 0; color: #ffffff; font-size: 20px; font-family: Arial, sans-serif;\">Sistema de Captação - Governo do Estado do Espírito Santo</h1></td></tr></table></td>";
		String campoTratamento = "Prezados(as), ";
		String campoCorpoPrincipal = "Solicitamos que seja elaborado o parecer necessário conforme sua área para o DIC via link abaixo.";
		String linkAcessarDic = "<p style=\"font-size: 14px; color: #000000; margin-bottom: 5px;\">Acesse o sistema SISCAP em:</p> <a href=\"" + envioDetalhesDto.linkAcessoProjeto() + "\" style=\"display: inline-block; padding: 10px 15px; text-decoration: none; border-radius: 4px; font-weight: bold; margin: 1px 0 1px 0; color: #2a8fbd;\"> " + envioDetalhesDto.linkAcessoProjeto() + "</a>";
		
		return "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: #f5f5f5;\">  <tr>  <td align=\"center\" style=\"padding: 20px 0;\">  <table width=\"800\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color: #ffffff; border-radius: 4px; box-shadow: 0 2px 4px #ffffff; max-width: 100%;\"> " + cabecalhoSuperior + " <tr><td style=\"padding: 0 12px;\"><hr style=\"border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;\"></td></tr> <tr> <td style=\"padding: 15px 20px; width:90%; background-color: #fcea8f; font-size: 14px;\"><strong style=\"color: #d32f2f;\">Atenção:</strong> Este é um e-mail automático. Favor não responder.</td></tr>   <tr><td style=\"padding: 0 20px;\"><hr style=\"border: 0; height: 0px; background-color: #e0e0e0; margin: 15px 0;\"></td></tr> <tr><td style=\"padding: 0 20px 20px 20px;\"> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\"> " + campoTratamento + " </p> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\"> " + campoCorpoPrincipal + "</p> " + linkAcessarDic + " </td> </tr> <td style=\"padding: 0 20px 20px 20px;\"> <p style=\"font-size: 14px; line-height: 1.5; margin-bottom: 20px;\">Em caso de dúvidas, estamos à disposição para prestar o suporte necessário.</p> </td> <tr> <td style=\"padding: 15px 20px; background-color: #2a8fbd; text-align: center; font-size: 12px; color: #ffffff;\">©2025 - Desenvolvido pela CGTI-SEP   <img src=\"cid:Icon-siscap\" style=\"height: 15px; vertical-align: middle; margin-right: 10px;\"> </td> </td>  </tr> </table> </td> </tr> </table>" ;
	
	}

}
