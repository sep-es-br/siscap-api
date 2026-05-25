package br.gov.es.siscap.dto;

public record ProjetoOdsDto(
        Integer idOdsProjeto,
        Integer odsId,
        Integer odsOrdem,
        String odsNome,
        String odsDescricao) {
}
