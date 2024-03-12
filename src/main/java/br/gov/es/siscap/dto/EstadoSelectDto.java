package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Estado;

public record EstadoSelectDto(
        Long id,
        String nome) {

    public EstadoSelectDto(Estado estado) {
        this(estado.getId(), estado.getNome());
    }

}
