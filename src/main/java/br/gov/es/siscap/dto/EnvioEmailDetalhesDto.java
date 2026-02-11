package br.gov.es.siscap.dto;

import java.util.List;
import java.util.Map;

public record EnvioEmailDetalhesDto(

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
        String protocoloEdocsPrograma,
        Map<String, String> subAssinantesEmails

) {

    public EnvioEmailDetalhesDto(
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
                camposSeremComplementados, null, "", "", "", null);
    }

    public EnvioEmailDetalhesDto(
            Long idPrograma,
            String nomeResponsavelEnvioEmail,
            String linkAcessoProjeto,
            List<String> emailsInteressadosList,
            String tituloPrograma,
            String siglaPrograma,
            String protocoloEdocsPrograma) {
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
                protocoloEdocsPrograma, null);
    }

    public EnvioEmailDetalhesDto(
            Long idPrograma,
            List<String> emailsInteressadosList,
            String tituloPrograma,
            String siglaPrograma,
            Map<String, String> subAssinantesEmails ) {
        this(
                null,
                "",
                "",
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
                "", subAssinantesEmails);
    }

}