package br.gov.es.siscap.dto;

public record GestaoPentahoBiDto(
    Integer idGestao,
    Integer ativa,
    String nomeGestao,
    String descricaoGestao,
    Integer deAno,
    Integer ateAno,
    String modelNameGestao) {

}
