package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Plano;

public record PlanoSelectDto(
        Long id,
        String nome) {

    public PlanoSelectDto(Plano plano) {
        this(plano.getId(), plano.getNome());
    }

}
