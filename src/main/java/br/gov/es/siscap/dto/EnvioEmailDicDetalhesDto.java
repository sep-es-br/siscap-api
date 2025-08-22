package br.gov.es.siscap.dto;

import java.util.List;

public record EnvioEmailDicDetalhesDto(
	String nomeResponsavelEnvioEmail,
	String linkAcessoProjeto,
	String descricaoOrganizacaoGestor,
	String nomeGestor,
	List<String> emailsInteressadosList,
	String tituloProjeto
) {

}