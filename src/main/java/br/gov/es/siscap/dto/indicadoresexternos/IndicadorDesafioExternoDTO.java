package br.gov.es.siscap.dto.indicadoresexternos;

public record IndicadorDesafioExternoDTO(
    Integer id,
    String nome,
    Integer grupoId,
    Integer subGrupoId) {
}
