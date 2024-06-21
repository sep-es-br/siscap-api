package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;

public record OrganizacaoDto(
        Long id,
        String nome,
        String nomeFantasia,
        String telefone,
        String cnpj,
        String email,
        String site,
        byte[] imagemPerfil,
        Long idOrganizacaoPai,
        Long idStatus,
        Long idPessoaResponsavel,
        Long idCidade,
        Long idEstado,
        Long idPais,
        Long idTipoOrganizacao) {

    public OrganizacaoDto(Organizacao organizacao, byte[] imagemPerfil) {
        this(organizacao.getId(), organizacao.getNome(), organizacao.getNomeFantasia(), organizacao.getTelefone(), organizacao.getCnpj(),
                organizacao.getEmail(), organizacao.getSite(), imagemPerfil,
                organizacao.getOrganizacaoPai() != null ? organizacao.getOrganizacaoPai().getId() : null,
                organizacao.getStatus() != null ? organizacao.getStatus().getId() : null,
                organizacao.getPessoa() != null ? organizacao.getPessoa().getId() : null,
                organizacao.getCidade() != null ? organizacao.getCidade().getId() : null,
                organizacao.getEstado() != null ? organizacao.getEstado().getId() : null,
                organizacao.getPais() != null ? organizacao.getPais().getId(): null,
                organizacao.getTipoOrganizacao() != null ? organizacao.getTipoOrganizacao().getId() : null);
    }

}
