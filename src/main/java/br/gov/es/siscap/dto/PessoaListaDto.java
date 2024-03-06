package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Pessoa;

public record PessoaListaDto(
        Long id,
        String nome,
        String email,
        String nomeImagem) {

    public PessoaListaDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getNome(), pessoa.getEmail(), pessoa.getNomeImagem());
    }
}
