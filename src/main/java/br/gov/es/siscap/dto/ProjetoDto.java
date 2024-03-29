package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Microrregiao;
import br.gov.es.siscap.models.Projeto;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public record ProjetoDto(
        Long id,
        String sigla,
        String titulo,
        BigInteger valorEstimado,
        String objetivo,
        String objetivoEspecifico,
        Long idStatus,
        Long idEntidade,
        String situacaoProblema,
        String solucoesPropostas,
        String impactos,
        String arranjosInstitucionais,
        List<Long> idMicrorregioes) {

    public ProjetoDto(Projeto projeto) {
        this(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
                projeto.getObjetivo(), projeto.getObjetivoEspecifico(), projeto.getStatus().getId(),
                projeto.getEntidade().getId(), projeto.getSituacaoProblema(), projeto.getSolucoesPropostas(),
                projeto.getImpactos(), projeto.getArranjosInstitucionais(),
                projeto.getMicrorregioes().stream().map(Microrregiao::getId).collect(Collectors.toList()));
    }

}
