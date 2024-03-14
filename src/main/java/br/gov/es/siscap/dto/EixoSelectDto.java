package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Eixo;

public record EixoSelectDto(
        Long id,
        String nome) {

    public EixoSelectDto(Eixo eixo) {
        this(eixo.getId(), eixo.getNome());
    }

}
