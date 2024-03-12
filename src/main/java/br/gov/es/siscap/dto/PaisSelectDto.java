package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Pais;

public record PaisSelectDto(
        Long id,
        String nome) {

    public PaisSelectDto(Pais pais) {
        this(pais.getId(), pais.getNome());
    }
}
