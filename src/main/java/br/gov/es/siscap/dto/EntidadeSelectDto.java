package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Entidade;

public record EntidadeSelectDto(
        Long id,
        String nome
) {

    public EntidadeSelectDto(Entidade entidade) {
        this(entidade.getId(), entidade.getNome());
    }

}
