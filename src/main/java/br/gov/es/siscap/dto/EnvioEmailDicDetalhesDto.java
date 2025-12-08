package br.gov.es.siscap.dto;

import java.util.List;

public record EnvioEmailDicDetalhesDto(
	String nomeResponsavelEnvioEmail,
	String linkAcessoProjeto,
	String descricaoOrganizacaoGestor,
	String nomeGestor,
	List<String> emailsInteressadosList,
	String tituloProjeto,
	String codigoMotivoArquivamento,
	String descricaoTipoMotivoArquivamento,
	String justificaivaArquivamento
) {

	public EnvioEmailDicDetalhesDto(
        String nomeResponsavelEnvioEmail,
        String linkAcessoProjeto,
        String descricaoOrganizacaoGestor,
        String nomeGestor,
        List<String> emailsInteressadosList,
        String tituloProjeto
    ) {
        this(
            nomeResponsavelEnvioEmail,
            linkAcessoProjeto,
            descricaoOrganizacaoGestor,
            nomeGestor,
            emailsInteressadosList,
            tituloProjeto,
            "",      // codigoMotivoArquivamento
            "",       // descricaoTipoMotivoArquivamento
			""
        );
    }

}