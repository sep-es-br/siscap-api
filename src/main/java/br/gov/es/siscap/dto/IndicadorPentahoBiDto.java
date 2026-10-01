package br.gov.es.siscap.dto;

public record IndicadorPentahoBiDto(
    Integer ativa,
    Integer idGestao,
    String nomeGestao,
    Integer idDesafio,
    String nomeDesafio,
    Integer idOrganizador,
    String nomeOrganizador,
    String modelNameOrganizador,
    Integer idIndicador,
    String nomeIndicador,
    String unidadeMedida,
	String polaridade,
	String medidoPor,
	Integer anoMeta,
	String valorMeta,
	Integer maiorAnoIndicador,
	Double maiorMetaIndicador
) {

}
