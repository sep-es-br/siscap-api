package br.gov.es.siscap.form;

import java.util.UUID;

public record ProjetoPessoaForm(
        Long idPessoa,
        UUID idPapelProjeto) {
}
