package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.PessoaOrganizacao;

import java.util.Set;

public record OrganizacaoDto(
			Long id,
			String nome,
			String abreviatura,
			String telefone,
			String cnpj,
			String email,
			String site,
			byte[] imagemPerfil,
			Long idOrganizacaoPai,
			Long idStatus,
			Long idCidade,
			Long idEstado,
			Long idPais,
			Long idTipoOrganizacao,
			Long idPessoaResponsavel) {

	public OrganizacaoDto(Organizacao organizacao, byte[] imagemPerfil) {
		this(organizacao.getId(), organizacao.getNome(), organizacao.getNomeFantasia(), organizacao.getTelefone(), organizacao.getCnpj(),
					organizacao.getEmail(), organizacao.getSite(), imagemPerfil,
					organizacao.getOrganizacaoPai() != null ? organizacao.getOrganizacaoPai().getId() : null,
					organizacao.getStatus() != null ? organizacao.getStatus().getId() : null,
					organizacao.getCidade() != null ? organizacao.getCidade().getId() : null,
					organizacao.getEstado() != null ? organizacao.getEstado().getId() : null,
					organizacao.getPais() != null ? organizacao.getPais().getId() : null,
					organizacao.getTipoOrganizacao() != null ? organizacao.getTipoOrganizacao().getId() : null, null);
	}

	public OrganizacaoDto(Organizacao organizacao, byte[] imagemPerfil, Set<PessoaOrganizacao> pessoaOrganizacaoSet) {
		this(organizacao.getId(), organizacao.getNome(), organizacao.getNomeFantasia(), organizacao.getTelefone(), organizacao.getCnpj(),
					organizacao.getEmail(), organizacao.getSite(), imagemPerfil,
					organizacao.getOrganizacaoPai() != null ? organizacao.getOrganizacaoPai().getId() : null,
					organizacao.getStatus() != null ? organizacao.getStatus().getId() : null,
					organizacao.getCidade() != null ? organizacao.getCidade().getId() : null,
					organizacao.getEstado() != null ? organizacao.getEstado().getId() : null,
					organizacao.getPais() != null ? organizacao.getPais().getId() : null,
					organizacao.getTipoOrganizacao() != null ? organizacao.getTipoOrganizacao().getId() : null,
					pessoaOrganizacaoSet.stream().filter(PessoaOrganizacao::getResponsavel).findFirst().map(pessoaOrganizacao -> pessoaOrganizacao.getPessoa().getId()).orElse(null));
	}
}
