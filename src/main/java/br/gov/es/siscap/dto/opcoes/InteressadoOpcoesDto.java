package br.gov.es.siscap.dto.opcoes;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;

import java.util.List;

public record InteressadoOpcoesDto(

			Long id,
			String nome,
			String email,
			List<Long> idsOrganizacoesList
) {

	public InteressadoOpcoesDto(Pessoa pessoa) {
		this(
					pessoa.getId(),
					pessoa.getNome(),
					pessoa.getEmail(),
					pessoa.getPessoaOrganizacaoSet().stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).toList()
		);
	}
}