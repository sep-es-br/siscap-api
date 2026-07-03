package br.gov.es.siscap.dto.indicadoresexternos;

public record OrganizadorGestaoPentahoBiDto(
    Integer idGestao,
    Integer idGrupo,
    String labelGrupo,
    String valorGrupo,
    Integer idSubGrupo,
    String labelSubGrupo,
    String valorSubGrupo
) {}
