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
    List<ProjetoCamposComplementacaoDto> camposSeremComplementados
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
			"", camposSeremComplementados
        );
    }

}