package br.gov.es.siscap.dto;

import java.util.List;

public record EnvioEmailDicDetalhesDto(
    Long idProjeto,
	String nomeResponsavelEnvioEmail,
	String linkAcessoProjeto,
	String descricaoOrganizacaoGestor,
	String nomeGestor,
	List<String> emailsInteressadosList,
	String tituloProjeto,
	String codigoMotivoArquivamento,
	String descricaoTipoMotivoArquivamento,
	String justificaivaArquivamento,
	String justificativaRevisao,
    List<ProjetoCamposComplementacaoDto> camposSeremComplementados,
    Long idPrograma,
    String tituloPrograma,
    String siglaPrograma,
    String protocoloEdocsPrograma
    
) {

	public EnvioEmailDicDetalhesDto(
        Long idProjeto,
        String nomeResponsavelEnvioEmail,
        String linkAcessoProjeto,
        String descricaoOrganizacaoGestor,
        String nomeGestor,
        List<String> emailsInteressadosList,
        String tituloProjeto,
        List<ProjetoCamposComplementacaoDto> camposSeremComplementados

    ) {
        this(
            idProjeto,
            nomeResponsavelEnvioEmail,
            linkAcessoProjeto,
            descricaoOrganizacaoGestor,
            nomeGestor,
            emailsInteressadosList,
            tituloProjeto,
            "",  
            "", 
			"",
			"",
            camposSeremComplementados, null, "", "", ""
        );
    }

    public EnvioEmailDicDetalhesDto(
        Long idPrograma,
        String nomeResponsavelEnvioEmail,
        String linkAcessoProjeto,
        List<String> emailsInteressadosList,
        String tituloPrograma,
        String siglaPrograma,
        String protocoloEdocsPrograma
    ) {
        this(
            null,
            nomeResponsavelEnvioEmail,
            linkAcessoProjeto,
            "",
            "",
            emailsInteressadosList,
            "",
            "",  
            "", 
			"",
			"",
            null, 
            idPrograma, 
            tituloPrograma,
            siglaPrograma,
            protocoloEdocsPrograma
        );
    }

}