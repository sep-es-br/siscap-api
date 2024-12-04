package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;

public record ProspeccaoOrganizacaoDetalhesDto(

			String nomeFantasia,
			String nome,
			String cidade,
			String estado,
			String pais,
			String telefone,
			String email
) {

	public ProspeccaoOrganizacaoDetalhesDto(Organizacao organizacao) {
		this(
					organizacao.getNomeFantasia(),
					organizacao.getNome(),
					organizacao.getCidade().getNome(),
					organizacao.getEstado().getNome(),
					organizacao.getPais().getNome(),
					organizacao.getTelefone(),
					organizacao.getEmail()
		);
	}
}