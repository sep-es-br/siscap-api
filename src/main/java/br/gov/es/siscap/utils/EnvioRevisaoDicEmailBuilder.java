package br.gov.es.siscap.utils;

public abstract class EnvioRevisaoDicEmailBuilder {

	private static final String CAMPO_TITULO = "Prezado(a),";
	private static final String CAMPO_CONTEUDO_TITULO = "Informamos que o projeto enviado para autuação necessita de revisão antes de sua continuidade no processo.\n";
	private static final String CAMPO_CONTEUDO_JUSTIFICATIVA = "Justificativa para o encaminhamento à revisão:";
	private static final String CAMPO_CONTEUDO_FINAL =	"Solicitamos que as correções sejam realizadas o mais breve possível para que o processo possa seguir normalmente.\n" + //
	"\n" + "Em caso de dúvidas, estamos à disposição.";
		
	public static String montarAssuntoEmail() {
		return "DIC para revisão.";
	}

	public static String montarCorpoEmail( String nomeResponsavelEnvioEmail, String justificativa  ) {
				
		String campoOperacaoTitulo = montarElementoTitulo( CAMPO_TITULO );
		String campoOperacaoConteudo = montarProjetoPropostoElementoConteudo( CAMPO_CONTEUDO_TITULO );
		String campoOperacaoConteudoJustificativa = montarElementoConteudo( CAMPO_CONTEUDO_JUSTIFICATIVA, justificativa );
		String campoOperacaoConteudoFinal = montarProjetoPropostoElementoConteudo( CAMPO_CONTEUDO_FINAL );
		String assinaturaPessoaEnviouDic = montarAssinaturaPessoaProspectora( nomeResponsavelEnvioEmail );
		
		String corpoEmail = "<html>" +
					"<body>" +

					"<p>" +
					campoOperacaoTitulo +
					campoOperacaoConteudo +
					"</p>" +

					"<p>" +
					campoOperacaoConteudoJustificativa +
					"</p>" +

					"<p>" +
					campoOperacaoConteudoFinal +
					"</p>" +
									
					"<div>" +
					assinaturaPessoaEnviouDic +
					"</div>" +

					"</body>" +
					"</html>";

		return corpoEmail;

	}
	
	private static String montarElementoTitulo(String titulo ) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + "</strong><br/>";
	}

	private static String montarElementoConteudo(String conteudo) {
		return "<span>" + conteudo + "</span><br/>";
	}

	private static String montarElementoConteudo(String conteudo, String justificativa) {
		return "<span>" + conteudo + "[" +  "<i>" + justificativa + "</i><br/>" + "]" + "</span><br/>";
	}

	private static String montarProjetoPropostoElementoConteudo( String conteudo ) {
		return  "<span style='margin-left: 8px;'>" + conteudo + "</span><br/>";
	}
	
	private static String montarAssinaturaPessoaProspectora(String nomeResponsavelEnvioEmail) {
		return montarElementoTitulo("Atenciosamente,") +
					montarElementoConteudo(nomeResponsavelEnvioEmail);
	}

}
