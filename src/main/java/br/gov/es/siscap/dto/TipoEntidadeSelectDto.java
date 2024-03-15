package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.TipoEntidade;

public record TipoEntidadeSelectDto(
        Long id,
        String nome
) {
    public TipoEntidadeSelectDto(TipoEntidade tipoEntidade) {
        this(tipoEntidade.getId(), tipoEntidade.getTipo());
    }
}
