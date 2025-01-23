package br.gov.es.siscap.utils;

import br.gov.es.siscap.dto.ProspeccaoDetalhesDto;
import br.gov.es.siscap.dto.ProspeccaoOrganizacaoDetalhesDto;
import br.gov.es.siscap.dto.ProspeccaoPessoaDetalhesDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public abstract class ProspeccaoEmailBuilder {

	private static final String CAMPO_ORGANIZACAO_PROSPECTORA_TITULO = "De:";
	private static final String CAMPO_ORGANIZACAO_PROSPECTADA_TITULO = "Para:";
	private static final String CAMPO_INTERESSADOS_TITULO = "Interessado(a)(s):";
	private static final String CAMPO_OPERACAO_TITULO = "Operação:";
	private static final String CAMPO_OBJETO_TITULO = "Objeto:";
	private static final String CAMPO_VALOR_ESTIMADO_TITULO = "Valor Estimado:";

	public static String montarAssuntoEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto) {
		String codigoCartaConsulta = prospeccaoDetalhesDto.cartaConsultaDetalhes().codigoCartaConsulta();
		String nomeObjetoCartaConsulta = prospeccaoDetalhesDto.cartaConsultaDetalhes().objeto().nome();

		return "Carta Consulta " + codigoCartaConsulta + " - " + nomeObjetoCartaConsulta;
	}

	public static String montarCorpoEmail(ProspeccaoDetalhesDto prospeccaoDetalhesDto) {
		String campoOrganizacaoProspectoraTitulo = montarElementoTitulo(CAMPO_ORGANIZACAO_PROSPECTORA_TITULO);
		String campoOrganizacaoProspectoraConteudo = montarCampoOrganizacaoDetalhesConteudo(prospeccaoDetalhesDto.organizacaoProspectoraDetalhes());

		String campoOrganizacaoProspectadaTitulo = montarElementoTitulo(CAMPO_ORGANIZACAO_PROSPECTADA_TITULO);
		String campoOrganizacaoProspectadaConteudo = montarCampoOrganizacaoDetalhesConteudo(prospeccaoDetalhesDto.organizacaoProspectadaDetalhes());

		String campoInteressadosTitulo = montarElementoTitulo(CAMPO_INTERESSADOS_TITULO);
		String campoInteressadosConteudo = montarCampoInteressadosConteudo(prospeccaoDetalhesDto.nomesInteressados());

		String campoOperacaoTitulo = montarElementoTitulo(CAMPO_OPERACAO_TITULO);
		String campoOperacaoConteudo = montarCampoOperacaoConteudo(prospeccaoDetalhesDto.tipoOperacao());

		String campoObjetoTitulo = montarElementoTitulo(CAMPO_OBJETO_TITULO);
		String campoObjetoConteudo = montarCampoObjetoConteudo(prospeccaoDetalhesDto.cartaConsultaDetalhes().objeto().nome());

		String campoValorEstimadoTitulo = montarElementoTitulo(CAMPO_VALOR_ESTIMADO_TITULO);
		String campoValorEstimadoConteudo = montarCampoValorEstimadoConteudo(prospeccaoDetalhesDto.cartaConsultaDetalhes().valor().quantia());

		String assinaturaPessoaProspectora = montarAssinaturaPessoaProspectora(prospeccaoDetalhesDto.pessoaProspectoraDetalhes());

		String corpoEmail = "<html>" +
					"<body>" +

					// Organização Prospectora
					"<p>" +
					campoOrganizacaoProspectoraTitulo +
					campoOrganizacaoProspectoraConteudo +
					"</p>" +

					// Organização Prospectada
					"<p>" +
					campoOrganizacaoProspectadaTitulo +
					campoOrganizacaoProspectadaConteudo +
					"</p>" +

					// Interessados
					"<p>" +
					campoInteressadosTitulo +
					campoInteressadosConteudo +
					"</p>" +

					// Operação
					"<p>" +
					campoOperacaoTitulo +
					campoOperacaoConteudo +
					"</p>" +

					// Objeto
					"<p>" +
					campoObjetoTitulo +
					campoObjetoConteudo +
					"</p>" +

					// Valor Estimado
					"<p>" +
					campoValorEstimadoTitulo +
					campoValorEstimadoConteudo +
					"</p>" +

					//Corpo Carta Consulta
					"<div>" +
					prospeccaoDetalhesDto.cartaConsultaDetalhes().corpo() +
					"</div>" +
					"<br />" +

					"<div>" +
					assinaturaPessoaProspectora +
					"</div>" +

					"</body>" +
					"</html>";

		return corpoEmail;
	}


	private static String montarElementoTitulo(String titulo) {
		return "<strong style='margin-bottom: 8px;'>" + titulo + "</strong><br/>";
	}

	private static String montarElementoConteudo(String conteudo) {
		return "<span>" + conteudo + "</span><br/>";
	}

	private static String montarCampoOrganizacaoDetalhesConteudo(ProspeccaoOrganizacaoDetalhesDto detalhesOrganizacao) {
		String nomeFantasia = detalhesOrganizacao.nomeFantasia();
		String nome = detalhesOrganizacao.nome();
		String endereco = detalhesOrganizacao.cidade() + ", " + detalhesOrganizacao.estado() + ", " + detalhesOrganizacao.pais();
		String telefone = detalhesOrganizacao.telefone();
		String email = detalhesOrganizacao.email();

		return montarElementoConteudo(nomeFantasia) +
					montarElementoConteudo(nome) +
					montarElementoConteudo(endereco) +
					montarElementoConteudo(telefone) +
					montarElementoConteudo(email);
	}

	private static String montarCampoInteressadosConteudo(List<String> nomesInteressados) {
		StringBuilder interessadosSB = new StringBuilder();

		for (String nomeInteressado : nomesInteressados) {
			interessadosSB.append(montarElementoConteudo(nomeInteressado));
		}

		return interessadosSB.toString();
	}

	private static String montarCampoOperacaoConteudo(String tipoOperacao) {
		return montarElementoConteudo(tipoOperacao);
	}

	private static String montarCampoObjetoConteudo(String nomeObjeto) {
		return montarElementoConteudo(nomeObjeto);
	}

	private static String montarCampoValorEstimadoConteudo(BigDecimal valorEstimado) {
		String valorEstimadoFormatado = NumberFormat.getCurrencyInstance().format(valorEstimado);

		return montarElementoConteudo(valorEstimadoFormatado);
	}

	private static String montarAssinaturaPessoaProspectora(ProspeccaoPessoaDetalhesDto pessoaProspectoraDetalhes) {
		return montarElementoTitulo("Atenciosamente,") +
					montarElementoConteudo(pessoaProspectoraDetalhes.nome()) +
					montarElementoConteudo(pessoaProspectoraDetalhes.email()) +
					montarElementoConteudo(pessoaProspectoraDetalhes.nomeOrganizacao());
	}
}
