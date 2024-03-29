package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.Microrregiao;
import br.gov.es.siscap.models.Projeto;

import java.math.BigInteger;
import java.util.List;

public record ProjetoListaDto(
        Long id,
        String sigla,
        String titulo,
        BigInteger valorEstimado,
        List<String> nomesMicrorregioes) {

    public ProjetoListaDto(Projeto projeto) {
        this(projeto.getId(), projeto.getSigla(), projeto.getTitulo(), projeto.getValorEstimado(),
                projeto.getMicrorregioes().stream().map(Microrregiao::getNome).toList());
    }

}
