package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Area;

public record AreaSelectDto(
        Long id,
        String nome) {

    public AreaSelectDto(Area area) {
        this(area.getId(), area.getNome());
    }

}
