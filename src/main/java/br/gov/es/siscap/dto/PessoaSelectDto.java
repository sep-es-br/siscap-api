package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Pessoa;

public record PessoaSelectDto(
        Long id,
        String nome) {

    public PessoaSelectDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getNome());
    }

}
