package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.*;

public record SelectDto(String id, String nome) {

    public SelectDto(Long id, String nome) {
        this(String.valueOf(id), nome);
    }

    public SelectDto(TipoOrganizacao tipoOrganizacao) {
        this(tipoOrganizacao.getId(), tipoOrganizacao.getTipo());
    }

    public SelectDto(Plano plano) {
        this(plano.getId(), plano.getNome());
    }

    public SelectDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getNome());
    }

    public SelectDto(Pais pais) {
        this(pais.getId(), pais.getNome());
    }

    public SelectDto(Organizacao organizacao) {
        this(organizacao.getId(), organizacao.getNome());
    }

    public SelectDto(Microrregiao microrregiao) {
        this(microrregiao.getId(), microrregiao.getNome());
    }

    public SelectDto(Estado estado) {
        this(estado.getId(), estado.getNome());
    }

    public SelectDto(Cidade cidade) {
        this(cidade.getId(), cidade.getNome());
    }

    public SelectDto(Eixo eixo) {
        this(eixo.getId(), eixo.getNome());
    }

    public SelectDto(Area area) {
        this(area.getId(), area.getNome());
    }

    public SelectDto(AreaAtuacao areaAtuacao) {
        this(areaAtuacao.getId(), areaAtuacao.getNome());
    }
}
