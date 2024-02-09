package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Microrregiao;

public record MicrorregiaoSelectDto(
        Long id,
        String nome) {

    public MicrorregiaoSelectDto(Microrregiao microrregiao) {
        this(microrregiao.getId(), microrregiao.getNome());
    }

}
