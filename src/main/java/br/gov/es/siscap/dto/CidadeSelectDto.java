package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Cidade;

public record CidadeSelectDto(
        Long id,
        String nome) {

    public CidadeSelectDto(Cidade cidade) {
        this(cidade.getId(), cidade.getNome());
    }

}
