package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.EnvioEmailDicDetalhesDto;

public abstract class EnvioAnaliseGestorDicEmailBuilder {

	private static final String CAMPO_TITULO = "Prezado(a) Gestor(a) da ";
	private static final String CAMPO_CONTEUDO = "Informo que há um DIC ( Documento Inicial Para Captação) disponível para análise e assinatura para autuação via E- Docs para a SUBCAP .<br/>" 
		+ "Para acessar a tela com os dados do DIC e efetivar a assinatura e autuação no E- Docs, por favor, utilize o seguinte link do sistema SISCAP :";
		
	public static String montarAssuntoEmail() {
		return "DIC para análise e deliberação.";
	}

	public static String montarCorpoEmail(EnvioEmailDicDetalhesDto envioDetalhesDto) {
				
		String campoOperacaoTitulo = montarElementoTitulo(CAMPO_TITULO, envioDetalhesDto);
		String campoOperacaoConteudo = montarProjetoPropostoElementoConteudo(CAMPO_CONTEUDO, envioDetalhesDto);
		String assinaturaPessoaEnviouDic = montarAssinaturaPessoaProspectora(envioDetalhesDto);
		
		String corpoEmail = "<html>" +
					"<body>" +

					"<p>" +
					campoOperacaoTitulo +
					campoOperacaoConteudo +
					"</p>" +
									
					"<div>" +
					assinaturaPessoaEnviouDic +
					"</div>" +

					"</body>" +
					"</html>";

		return corpoEmail;

	}

	private static String montarElementoTitulo(String titulo, EnvioEmailDicDetalhesDto envioDetalhes ) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + ", " + envioDetalhes.descricaoOrganizacaoGestor() + "</strong><br/>";
	}

	private static String montarElementoTitulo(String titulo ) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + "</strong><br/>";
	}

	private static String montarElementoConteudo(String conteudo) {
		return "<span>" + conteudo + "</span><br/>";
	}

	private static String montarProjetoPropostoElementoConteudo(String conteudo, EnvioEmailDicDetalhesDto envioDetalhes) {
		return  "<span style='margin-left: 8px;'>" + conteudo + "</span><br/>" +
		"<a href='" + envioDetalhes.linkAcessoProjeto() + "' " +
		"style='color: #1155cc; text-decoration: underline;'>" +
		"Acessar Projeto</a>";
	}
	
	private static String montarAssinaturaPessoaProspectora(EnvioEmailDicDetalhesDto envioDetalhes) {
		return montarElementoTitulo("Atenciosamente,") +
					montarElementoConteudo(envioDetalhes.nomeResponsavelEnvioEmail()) +
					montarElementoConteudo(envioDetalhes.descricaoOrganizacaoGestor());
	}

}
