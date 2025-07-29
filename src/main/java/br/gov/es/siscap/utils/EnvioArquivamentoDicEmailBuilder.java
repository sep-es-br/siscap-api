package br.gov.es.siscap.utils;

public abstract class EnvioArquivamentoDicEmailBuilder {

	private static final String CAMPO_TITULO = "Prezado(a),";
	private static final String CAMPO_CONTEUDO_TITULO = "Informamos que o projeto %s, anteriormente encaminhado para análise, será arquivado.";
	private static final String CAMPO_CONTEUDO_JUSTIFICATIVA = "Motivo do arquivamento:<br/>";
	private static final String CAMPO_CONTEUDO_FINAL =	"Caso haja necessidade de esclarecimentos adicionais, estamos à disposição para auxiliá-lo(a).<br/>" ;
		
	public static String montarAssuntoEmail(String descricaoProjeto) {
		return "Arquivamento do Projeto " + descricaoProjeto + ".";
	}

	public static String montarCorpoEmail( String nomeResponsavelEnvioEmail, String justificativa, String descricaoProjeto  ) {
				
		String campoOperacaoTitulo = montarElementoTitulo( CAMPO_TITULO );
		String campoOperacaoConteudo = montarProjetoPropostoElementoConteudo( CAMPO_CONTEUDO_TITULO, descricaoProjeto);
		String campoOperacaoConteudoJustificativa = montarElementoConteudoJustificativa( CAMPO_CONTEUDO_JUSTIFICATIVA, justificativa );
		String campoOperacaoConteudoFinal = montarProjetoPropostoElementoConteudo( CAMPO_CONTEUDO_FINAL, descricaoProjeto );
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

	private static String montarElementoConteudoJustificativa(String conteudo, String justificativa) {
		return "<span>" + conteudo + "[ " +  "<i>" + justificativa + "</i>" + " ]" + "</span><br/>";
	}

	private static String montarProjetoPropostoElementoConteudo( String conteudo, String descricaoProjeto ) {
		return  "<span style='margin-left: 8px;'>" + String.format(conteudo, descricaoProjeto) + "</span><br/>";
	}
	
	private static String montarAssinaturaPessoaProspectora(String nomeResponsavelEnvioEmail) {
		return montarElementoTitulo("Atenciosamente,") +
					montarElementoConteudo(nomeResponsavelEnvioEmail);
	}

}
