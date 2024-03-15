package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Entidade;

public record EntidadeListaDto(
        Long id,
        String abreviatura,
        String nome,
        String nomeTipoEntidade,
        String telefone,
        String site,
        byte[] imagemPerfil) {

    public EntidadeListaDto(Entidade entidade, byte[] imagemPerfil) {
        this(entidade.getId(), entidade.getAbreviatura(), entidade.getNome(), entidade.getTipoEntidade().getTipo(),
                entidade.getTelefone(), entidade.getSite(), imagemPerfil);
    }

}
