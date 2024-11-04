package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.models.TipoOperacao;
import br.gov.es.siscap.models.*;

public record OpcoesDto(Long id, String nome) {

	public OpcoesDto(TipoOrganizacao tipoOrganizacao) {
		this(tipoOrganizacao.getId(), tipoOrganizacao.getTipo());
	}

	public OpcoesDto(Plano plano) {
		this(plano.getId(), plano.getNome());
	}

	public OpcoesDto(Pessoa pessoa) {
		this(pessoa.getId(), pessoa.getNome());
	}

	public OpcoesDto(Pais pais) {
		this(pais.getId(), pais.getNome());
	}

	public OpcoesDto(Organizacao organizacao) {
		this(organizacao.getId(), (organizacao.getNomeFantasia()) + " - " + organizacao.getNome());
	}

	public OpcoesDto(Microrregiao microrregiao) {
		this(microrregiao.getId(), microrregiao.getNome());
	}

	public OpcoesDto(Estado estado) {
		this(estado.getId(), estado.getNome());
	}

	public OpcoesDto(Cidade cidade) {
		this(cidade.getId(), cidade.getNome());
	}

	public OpcoesDto(Eixo eixo) {
		this(eixo.getId(), eixo.getNome());
	}

	public OpcoesDto(Area area) {
		this(area.getId(), area.getNome());
	}

	public OpcoesDto(AreaAtuacao areaAtuacao) {
		this(areaAtuacao.getId(), areaAtuacao.getNome());
	}

	public OpcoesDto(TipoPapel tipoPapel) {
		this(tipoPapel.getId(), tipoPapel.getTipo());
	}

	public OpcoesDto(Projeto projeto) {
		this(projeto.getId(), (projeto.getSigla() + " - " + projeto.getTitulo()));
	}

	public OpcoesDto(Programa programa) {
		this(programa.getId(), (programa.getSigla() + " - " + programa.getTitulo()));
	}

	public OpcoesDto(TipoValor tipoValor) {
		this(tipoValor.getId(), tipoValor.getTipo());
	}

	public OpcoesDto(TipoOperacao tipoOperacao) {
		this(tipoOperacao.getId(), tipoOperacao.getTipo());
	}
}