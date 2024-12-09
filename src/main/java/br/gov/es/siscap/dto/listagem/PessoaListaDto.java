package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Pessoa;

import java.util.List;

public record PessoaListaDto(

			Long id,
			String nome,
			String email,
			List<String> nomesOrganizacoes,
			byte[] imagemPerfil
) {

	public PessoaListaDto(Pessoa pessoa, byte[] imagemPerfil, List<String> nomesOrganizacoes) {
		this(
					pessoa.getId(),
					pessoa.getNome(),
					pessoa.getEmail(),
					nomesOrganizacoes,
					imagemPerfil
		);
	}
}
