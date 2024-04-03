package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.TipoOrganizacao;

public record TipoOrganizacaoSelectDto(
        Long id,
        String nome
) {
    public TipoOrganizacaoSelectDto(TipoOrganizacao tipoOrganizacao) {
        this(tipoOrganizacao.getId(), tipoOrganizacao.getTipo());
    }
}
