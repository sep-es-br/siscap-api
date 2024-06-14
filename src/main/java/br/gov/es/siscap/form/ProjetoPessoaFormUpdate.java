package br.gov.es.siscap.form;

import java.util.UUID;

public record ProjetoPessoaFormUpdate(
        Long id,
        Long idProjeto,
        Long idPessoa,
        UUID idPapelProjeto) {
}
