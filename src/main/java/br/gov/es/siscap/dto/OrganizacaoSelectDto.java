package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Organizacao;

public record OrganizacaoSelectDto(
        Long id,
        String nome
) {

    public OrganizacaoSelectDto(Organizacao organizacao) {
        this(organizacao.getId(), organizacao.getNome());
    }

}
