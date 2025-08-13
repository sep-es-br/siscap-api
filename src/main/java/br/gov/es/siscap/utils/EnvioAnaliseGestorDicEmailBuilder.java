package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;

public abstract class EnvioAnaliseGestorDicEmailBuilder {

	private static final String CAMPO_TITULO = "Prezado(a) Gestor(a) do(a) %s, ";
	private static final String CAMPO_CONTEUDO = "Informamos que o DIC (Documento Inicial para Captação) do projeto [%s] já se encontra disponível no SISCAP para análise, assinatura e posterior autuação via E-Docs pela SUBCAP.\r\n.<br/>" 
		+ "Para acessar diretamente o documento, utilize o link abaixo:";
	private static final String CAMPO_RODAPE = "Em caso de dúvidas, estamos à disposição para prestar o suporte necessário.";		

	public static String montarAssuntoEmail() {
		return "DIC disponível para análise e autuação via E-Docs.";
	}

	public static String montarCorpoEmail(EnvioEmailDicDetalhesDto envioDetalhesDto) {
				
		String campoOperacaoTitulo = montarElementoTitulo(CAMPO_TITULO, envioDetalhesDto);
		String campoOperacaoConteudo = montarProjetoPropostoElementoConteudo(CAMPO_CONTEUDO, envioDetalhesDto);
		String campoRodape = montarElementoConteudo(CAMPO_RODAPE);
		
		String corpoEmail = "<html>" +
					"<body>" +
					"<p>" +
					campoOperacaoTitulo +
					campoOperacaoConteudo +
					"</p>" +		
					"<div>" +
					campoRodape +
					"</div>" +
					"</body>" +
					"</html>";

		return corpoEmail;

	}

	private static String montarElementoTitulo(String titulo, EnvioEmailDicDetalhesDto envioDetalhes ) {
		return "<strong style='margin-bottom: 8px;'>" + String.format( titulo, envioDetalhes.descricaoOrganizacaoGestor() ) + "</strong><br/>";
	}

	/*
	private static String montarElementoTitulo(String titulo ) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + "</strong><br/>";
	}
	*/

	private static String montarElementoConteudo(String conteudo) {
		return "<span>" + conteudo + "</span><br/>";
	}

	private static String montarProjetoPropostoElementoConteudo(String conteudo, EnvioEmailDicDetalhesDto envioDetalhes) {
		return  "<span style='margin-left: 8px;'>" + String.format(conteudo, envioDetalhes.tituloProjeto() ) + "</span><br/>" +
		"<a href='" + envioDetalhes.linkAcessoProjeto() + "' " +
		"style='color: #1155cc; text-decoration: underline;'>" +
		"Acessar Projeto</a>";
	}

	/*
	private static String montarAssinaturaPessoaProspectora(EnvioEmailDicDetalhesDto envioDetalhes) {
		return montarElementoTitulo("Atenciosamente,") +
					montarElementoConteudo(envioDetalhes.nomeResponsavelEnvioEmail()) +
					montarElementoConteudo(envioDetalhes.descricaoOrganizacaoGestor());
	}
	*/

}
