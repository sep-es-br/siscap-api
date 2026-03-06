package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProgramaOrganizacao;

public record ProgramaOrganizacaoDto(
        Long idPrograma,
        Long id,
        Integer papel) {

    public ProgramaOrganizacaoDto(ProgramaOrganizacao programaOrganizacao) {
        this( programaOrganizacao.getPrograma().getId(), 
                programaOrganizacao.getOrganizacao().getId(),
                programaOrganizacao.getTipoOrganizacao() );
    }

}
