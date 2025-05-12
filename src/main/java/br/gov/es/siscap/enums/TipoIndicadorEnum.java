package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoIndicadorEnum {

    EXTREMA_POBREZA(1L, "Taxa de extrema pobreza"),
    RAZAO_RENDA(2L, "Razão entre as rendas dos 10% mais ricos e os 40% mais pobres"),
    RENDA_PER_CAPITA(3L, "Rendimentos domiciliar per capita nas grandes regiões"),
    PIB_PER_CAPITA(4L, "Produto interno bruto per capita em R$ do último ano"),
    TAXA_DESEMPREGO(5L, "Taxa de desemprego (média anual)"),
    EMISSAO_GEE(6L, "Emissão de Gases de Efeito Estufa (GEE) no Brasil"),
    DESMATAMENTO_AMAZONIA(7L, "Desmatamento anual do bioma Amazônia (em Km²)");

    private final Long id;
    private final String descricao;


}