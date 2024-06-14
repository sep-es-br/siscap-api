package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoPessoa;

import java.util.UUID;

public record ProjetoPessoaSelectDto(
        Long id,
        Long idPessoa,
        String nomePessoa,
        UUID idPapel,
        String nomePapel) {

    public ProjetoPessoaSelectDto(ProjetoPessoa projetoPessoa) {
        this(projetoPessoa.getId(), projetoPessoa.getPessoa().getId(), projetoPessoa.getPessoa().getNome(),
                projetoPessoa.getPapelProjeto().getId(), projetoPessoa.getPapelProjeto().getNome());
    }
}
