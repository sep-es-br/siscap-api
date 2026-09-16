package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.AreaAtuacao;
import br.gov.es.siscap.models.Pessoa;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record PessoaDto(
		Long id,
		String nome,
		String nomeSocial,
		String nacionalidade,
		String email,
		String telefoneComercial,
		String telefonePessoal,
		Set<Long> idAreasAtuacao,
		byte[] imagemPerfil,
		Set<Long> idOrganizacoes,
		Long idOrganizacaoResponsavel) {

	public PessoaDto(Pessoa pessoa, byte[] imagemPerfil, Set<Long> idOrganizacoes, Long idOrganizacaoResponsavel) {
		this(
			pessoa.getId(),
			pessoa.getNome(),
			pessoa.getNomeSocial(),
			pessoa.getNacionalidade(),
			pessoa.getEmail(),
			pessoa.getTelefoneComercial(),
			pessoa.getTelefonePessoal(),
			pessoa.getAreasAtuacao() != null ? pessoa.getAreasAtuacao().stream().filter(Objects::nonNull).map(AreaAtuacao::getId).collect(Collectors.toSet()) : null,
			imagemPerfil,
			idOrganizacoes,
			idOrganizacaoResponsavel);
	}

}
