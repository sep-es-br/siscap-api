package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;

import java.util.List;
import java.util.Set;

public record PessoaListaDto(
			Long id,
			String nome,
			String email,
			List<String> nomesOrganizacoes,
			byte[] imagemPerfil) {

	public PessoaListaDto(Pessoa pessoa, byte[] imagemPerfil, Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		this(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(),
					pessoaOrganizacaoSet.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getNome).toList(),
					imagemPerfil);
	}
}
