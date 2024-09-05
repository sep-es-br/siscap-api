package br.gov.es.siscap.dto.listagem;

import br.gov.es.siscap.models.Projeto;

import java.math.BigDecimal;
import java.util.List;

public record ProjetoListaDto(
        Long id,
        String sigla,
        String titulo,
        BigDecimal valorEstimado,
        List<String> nomesMicrorregioesRateio
) {
    public ProjetoListaDto(Projeto projeto, List<String> nomesMicrorregioesRateio) {
        this(
              projeto.getId(),
              projeto.getSigla(),
              projeto.getTitulo(),
              projeto.getValorEstimado(),
              nomesMicrorregioesRateio
        );
    }
}
