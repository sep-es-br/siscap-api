package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;

public record ProspeccaoPessoaDetalhesDto(
			String nome,
			String email,
			String nomeOrganizacao
) {

	public ProspeccaoPessoaDetalhesDto(Pessoa pessoaProspectora, Organizacao organizacaoProspectora) {
		this(
					pessoaProspectora.getNome(),
					pessoaProspectora.getEmail(),
					organizacaoProspectora.getNome()
		);
	}
}