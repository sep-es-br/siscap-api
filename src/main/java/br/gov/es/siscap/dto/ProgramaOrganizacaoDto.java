package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProgramaOrganizacao;

public record ProgramaOrganizacaoDto(
        Long id,
        Long idPrograma,
        Long idOrganizacao,
        Integer tipoOrganizacao) {

    public ProgramaOrganizacaoDto(Long idPrograma, Long idOrganizacao, Integer tipoOrganizacao) {
        this(null, idPrograma, idOrganizacao, tipoOrganizacao);
    }

    public ProgramaOrganizacaoDto(ProgramaOrganizacao programaOrganizacao) {
        this(programaOrganizacao.getId(),
                programaOrganizacao.getPrograma().getId(), programaOrganizacao.getOrganizacao().getId(),
                programaOrganizacao.getTipoOrganizacao());
    }

}
