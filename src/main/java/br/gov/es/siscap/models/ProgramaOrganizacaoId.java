package br.gov.es.siscap.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ProgramaOrganizacaoId implements Serializable {

    private Long programa;
    private Long organizacao;

    public ProgramaOrganizacaoId() {}

    public ProgramaOrganizacaoId(Long programa, Long organizacao) {
        this.programa = programa;
        this.organizacao = organizacao;
    }

}
