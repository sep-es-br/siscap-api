package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Prospeccao;

import java.util.List;

public record ProspeccaoDetalhesDto(

			Long id,
			String codigoProspeccao,
			String statusProspeccao,
			ProspeccaoPessoaDetalhesDto pessoaProspectoraDetalhes,
			ProspeccaoOrganizacaoDetalhesDto organizacaoProspectoraDetalhes,
			ProspeccaoOrganizacaoDetalhesDto organizacaoProspectadaDetalhes,
			List<String> nomesInteressados,
			CartaConsultaDetalhesDto cartaConsultaDetalhes,
			String tipoOperacao
) {

	public ProspeccaoDetalhesDto(Prospeccao prospeccao, List<String> nomesInteressados, CartaConsultaDetalhesDto cartaConsultaDetalhesDto) {
		this(
					prospeccao.getId(),
					(prospeccao.getCartaConsulta().gerarCodigoCartaConsulta() + '-' + prospeccao.getCountAno()),
					prospeccao.getStatusProspeccao(),
					new ProspeccaoPessoaDetalhesDto(prospeccao.getPessoaProspectora(), prospeccao.getOrganizacaoProspectora()),
					new ProspeccaoOrganizacaoDetalhesDto(prospeccao.getOrganizacaoProspectora()),
					new ProspeccaoOrganizacaoDetalhesDto(prospeccao.getOrganizacaoProspectada()),
					nomesInteressados,
					cartaConsultaDetalhesDto,
					prospeccao.getCartaConsulta().getTipoOperacao().getTipo()
		);
	}
}