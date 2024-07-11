package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Pessoa;

public record PessoaListaDto(
			Long id,
			String nome,
			String email,
			String nomeOrganizacao,
			byte[] imagemPerfil) {

	public PessoaListaDto(Pessoa pessoa, byte[] imagemPerfil) {
		this(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(),
					pessoa.buscarPessoaOrganizacaoPorPessoa() != null ? pessoa.buscarPessoaOrganizacaoPorPessoa().getOrganizacao().getNome() : null,
					imagemPerfil);
	}
}
