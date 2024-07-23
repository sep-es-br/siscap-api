package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.AreaAtuacao;
import br.gov.es.siscap.models.Pessoa;

import java.util.Set;
import java.util.stream.Collectors;

public record PessoaDto(
			Long id,
			String nome,
			String nomeSocial,
			String nacionalidade,
			String genero,
			String cpf,
			String email,
			String telefoneComercial,
			String telefonePessoal,
			EnderecoDto endereco,
			Set<String> idAreasAtuacao,
			byte[] imagemPerfil,
			Long idOrganizacao,
			Boolean isResponsavelOrganizacao) {

	public PessoaDto(Pessoa pessoa, byte[] imagemPerfil) {
		this(pessoa.getId(), pessoa.getNome(), pessoa.getNomeSocial(), pessoa.getNacionalidade(), pessoa.getGenero(),
					pessoa.getCpf(), pessoa.getEmail(), pessoa.getTelefoneComercial(), pessoa.getTelefonePessoal(),
					pessoa.getEndereco() != null ? new EnderecoDto(pessoa.getEndereco()) : null, pessoa.getAreasAtuacao() != null ?
								pessoa.getAreasAtuacao().stream().map(AreaAtuacao::getId).collect(Collectors.toSet()) : null,
					imagemPerfil,
					pessoa.buscarPessoaOrganizacaoPorPessoa() != null ? pessoa.buscarPessoaOrganizacaoPorPessoa().getOrganizacao().getId() : null,
					pessoa.buscarPessoaOrganizacaoPorPessoa() != null ? pessoa.buscarPessoaOrganizacaoPorPessoa().getResponsavel() : null);
	}
}
