package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.AreaAtuacao;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;

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
			Set<Long> idAreasAtuacao,
			byte[] imagemPerfil,
			Set<Long> idOrganizacoes,
			Long idOrganizacaoResponsavel) {

	public PessoaDto(Pessoa pessoa, byte[] imagemPerfil) {
		this(pessoa.getId(), pessoa.getNome(), pessoa.getNomeSocial(), pessoa.getNacionalidade(), pessoa.getGenero(),
					pessoa.getCpf(), pessoa.getEmail(), pessoa.getTelefoneComercial(), pessoa.getTelefonePessoal(),
					pessoa.getEndereco() != null ? new EnderecoDto(pessoa.getEndereco()) : null, pessoa.getAreasAtuacao() != null ?
								pessoa.getAreasAtuacao().stream().map(AreaAtuacao::getId).collect(Collectors.toSet()) : null,
					imagemPerfil,
					null, null);
	}

	public PessoaDto(Pessoa pessoa, byte[] imagemPerfil, Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		this(pessoa.getId(), pessoa.getNome(), pessoa.getNomeSocial(), pessoa.getNacionalidade(), pessoa.getGenero(),
					pessoa.getCpf(), pessoa.getEmail(), pessoa.getTelefoneComercial(), pessoa.getTelefonePessoal(),
					pessoa.getEndereco() != null ? new EnderecoDto(pessoa.getEndereco()) : null, pessoa.getAreasAtuacao() != null ?
								pessoa.getAreasAtuacao().stream().map(AreaAtuacao::getId).collect(Collectors.toSet()) : null,
					imagemPerfil,
					pessoaOrganizacaoSet.stream().map(PessoaOrganizacao::getOrganizacao).map(Organizacao::getId).collect(Collectors.toSet()),
					0L);
//					pessoaOrganizacaoSet.stream().filter(PessoaOrganizacao::getResponsavel).findFirst().map(pessoaOrganizacao -> pessoaOrganizacao.getOrganizacao().getId()).orElse(null));
	}
}
