package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Microregiao;

public record MicroregiaoSelectDto(
        Long id,
        String nome) {

    public MicroregiaoSelectDto(Microregiao microregiao) {
        this(microregiao.getId(), microregiao.getNome());
    }

}
