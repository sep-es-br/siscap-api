package br.gov.es.siscap.form;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * Formulário para atualizar um projeto. Não deve ser usado fora desse contexto.
 * @param sigla
 * @param titulo
 * @param idEntidade
 * @param valorEstimado
 * @param idMicrorregioes
 * @param objetivo
 * @param objetivoEspecifico
 * @param situacaoProblema
 * @param solucoesPropostas
 * @param impactos
 * @param arranjosInstitucionais
 */
public record ProjetoUpdateForm(
        @Size(max = 12)
        String sigla,
        @Size(max = 150)
        String titulo,
        @Positive
        Long idEntidade,
        @Positive
        BigDecimal valorEstimado,
        List<Long> idMicrorregioes,
        @Size(max = 2000)
        String objetivo,
        @Size(max = 2000)
        String objetivoEspecifico,
        @Size(max = 2000)
        String situacaoProblema,
        @Size(max = 2000)
        String solucoesPropostas,
        @Size(max = 2000)
        String impactos,
        @Size(max = 2000)
        String arranjosInstitucionais,
        List<Long> idPessoasEquipeElab,
        Long idPlano) {
}
