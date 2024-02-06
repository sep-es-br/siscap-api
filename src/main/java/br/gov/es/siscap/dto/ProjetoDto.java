package br.gov.es.siscap.dto;

import br.gov.es.siscap.entity.Projeto;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ProjetoDto(
        Integer id,
        String sigla,
        String titulo,
        BigInteger valorEstimado,
        String objetivo,
        String objetivoEspecifico,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        LocalDateTime apagadoEm) {

    public ProjetoDto(Projeto projeto) {
        this(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
                projeto.getObjetivo(), projeto.getObjetivoEspecifico(), projeto.getCriadoEm(),
                projeto.getAtualizadoEm(), projeto.getApagadoEm());
    }

}
