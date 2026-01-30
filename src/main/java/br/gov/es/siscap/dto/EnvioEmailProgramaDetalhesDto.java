package br.gov.es.siscap.dto;

import java.util.List;

public record EnvioEmailProgramaDetalhesDto(
    Long idPrograma,
	String nomeResponsavelEnvioEmail,
	String linkAcessoPrograma,
	List<String> emailsInteressadosList,
	String tituloPrograma
) {

}