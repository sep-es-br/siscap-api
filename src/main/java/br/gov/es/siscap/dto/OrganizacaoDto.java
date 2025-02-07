package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;

public record OrganizacaoDto(

			Long id,
			String guid,
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
			Long idPessoaResponsavel
) {

	public OrganizacaoDto(Organizacao organizacao, byte[] imagemPerfil, Long idPessoaResponsavel) {
		this(
					organizacao.getId(),
					organizacao.getGuid(),
					organizacao.getNome(),
					organizacao.getNomeFantasia(),
					organizacao.getTelefone(),
					organizacao.getCnpj(),
					organizacao.getEmail(),
					organizacao.getSite(),
					imagemPerfil,
					organizacao.getOrganizacaoPai() != null ? organizacao.getOrganizacaoPai().getId() : null,
					organizacao.getTipoStatus() != null ? organizacao.getTipoStatus().getId() : null,
					organizacao.getCidade() != null ? organizacao.getCidade().getId() : null,
					organizacao.getEstado() != null ? organizacao.getEstado().getId() : null,
					organizacao.getPais() != null ? organizacao.getPais().getId() : null,
					organizacao.getTipoOrganizacao() != null ? organizacao.getTipoOrganizacao().getId() : null,
					idPessoaResponsavel
		);
	}
}