package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoPessoa;

import java.util.UUID;

public record ProjetoPessoaDto(
        Long id,
        Long idProjeto,
        Long idPessoa,
        UUID idPapelProjeto) {

    public ProjetoPessoaDto(ProjetoPessoa projetoPessoa) {
        this(projetoPessoa.getId(), projetoPessoa.getProjeto().getId(), projetoPessoa.getPessoa().getId(),
                projetoPessoa.getPapelProjeto().getId());
    }

}
