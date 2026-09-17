package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProjetoAcaoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorAvulsoDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.dto.ProjetoParecerDto;
import br.gov.es.siscap.dto.ProjetoPlanejamentoPpaLoaDto;
import br.gov.es.siscap.dto.RateioDto;
import br.gov.es.siscap.dto.ValorDto;
import br.gov.es.siscap.validation.groups.ValidacaoEnvio;
import br.gov.es.siscap.validation.groups.ValidacaoRascunho;
import br.gov.es.siscap.dto.ProjetoOdsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record ProjetoForm(

        @NotBlank(
                message = "Sigla é obrigatória",
                groups = ValidacaoRascunho.class
        )
        @Size(max = 12)
        String sigla,

        @NotBlank(
                message = "Título é obrigatório",
                groups = ValidacaoRascunho.class
        )
        @Size(max = 150)
        String titulo,

        @Positive
        Long idResponsavelProponente,

        @NotEmpty(
                message = "Equipe de elaboração é obrigatória",
                groups = ValidacaoRascunho.class
        )
        @Valid
        List<EquipeDto> equipeElaboracao,

        @NotNull(
                message = "Organização é obrigatória",
                groups = ValidacaoRascunho.class
        )
        @Positive
        Long idOrganizacao,

        @Valid
        ValorDto valor,

        @Valid
        List<RateioDto> rateio,

        @NotBlank(
                message = "Objetivo é obrigatório",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String objetivo,

        @NotBlank(
                message = "Objetivo específico é obrigatório",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String objetivoEspecifico,

        @NotBlank(
                message = "Situação problema é obrigatória",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String situacaoProblema,

        @NotBlank(
                message = "Soluções propostas são obrigatórias",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String solucoesPropostas,

        @NotBlank(
                message = "Impactos é obrigatório",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String impactos,

        @NotBlank(
                message = "Arranjos institucionais é obrigatório",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String arranjosInstitucionais,

        @Valid
        List<ProjetoIndicadorDto> indicadoresProjeto,

        @Valid
        List<ProjetoAcaoDto> acoesProjeto,

        @NotBlank(
                message = "Peças de planejamento é obrigatório",
                groups = ValidacaoEnvio.class
        )
        @Size(max = 2000)
        String pecasPlanejamento,

        boolean enviarProjetoGestor,

        @Size(max = 15)
        String protocoloEdocs,

        boolean enviarProjetoPedirParecer,

        @Valid
        ProjetoParecerDto parecerProjetoUsuario,

        @Valid
        List<ProjetoIndicadorAvulsoDto> indicadoresAvulsosProjeto,

        @Valid
        List<ProjetoOdsDto> odsProjeto,

        @Valid
        List<ProjetoPlanejamentoPpaLoaDto> acoesPlanejamentoProjeto,

        Boolean naoPrevistoNoPpa

) {
}