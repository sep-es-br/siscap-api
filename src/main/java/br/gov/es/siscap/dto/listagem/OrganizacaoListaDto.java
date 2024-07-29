package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Organizacao;

public record OrganizacaoListaDto(
        Long id,
        String nomeFantasia,
        String nome,
        String nomeTipoOrganizacao,
        String telefone,
        String site,
        byte[] imagemPerfil) {

    public OrganizacaoListaDto(Organizacao organizacao, byte[] imagemPerfil) {
        this(organizacao.getId(), organizacao.getNomeFantasia(), organizacao.getNome(), organizacao.getTipoOrganizacao().getTipo(),
                organizacao.getTelefone(), organizacao.getSite(), imagemPerfil);
    }

}
