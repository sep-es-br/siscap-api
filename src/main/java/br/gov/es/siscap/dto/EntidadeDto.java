package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Entidade;

public record EntidadeDto(
        Long id,
        String nome,
        String abreviatura,
        String telefone,
        String cnpj,
        String fax,
        String email,
        String site,
        byte[] imagemPerfil,
        Long idEntidadePai,
        Long idStatus,
        Long idPessoaResponsavel,
        Long idCidade,
        Long idPais,
        Long idTipoEntidade) {

    public EntidadeDto(Entidade entidade, byte[] imagemPerfil) {
        this(entidade.getId(), entidade.getNome(), entidade.getAbreviatura(), entidade.getTelefone(), entidade.getCnpj(),
                entidade.getFax(), entidade.getEmail(), entidade.getSite(), imagemPerfil,
                entidade.getEntidadePai() != null ? entidade.getEntidadePai().getId() : null,
                entidade.getStatus() != null ? entidade.getStatus().getId() : null,
                entidade.getPessoa() != null ? entidade.getPessoa().getId() : null,
                entidade.getCidade() != null ? entidade.getCidade().getId() : null,
                entidade.getPais() != null ? entidade.getPais().getId(): null,
                entidade.getTipoEntidade() != null ? entidade.getTipoEntidade().getId() : null);
    }

}
