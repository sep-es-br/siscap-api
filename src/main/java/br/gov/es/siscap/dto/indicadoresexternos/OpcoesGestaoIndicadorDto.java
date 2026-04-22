package br.gov.es.siscap.dto.indicadoresexternos;

import java.util.List;

import br.gov.es.siscap.models.IndicadorGestaoExterno;

public record OpcoesGestaoIndicadorDto() {

    public static OpcoesGestaoIndicadorDto toDTO(IndicadorGestaoExterno gestao) {
 
        // List<LabelDTO> labels = gestao.getLabels().stream()
        //         .map(IndicadorGestaoDetalheMapper::toLabelDTO)
        //         .toList();
 
        // return new IndicadorGestaoDetalheDTO(
        //         gestao.getId(),
        //         gestao.getNome(),
        //         gestao.getAtiva(),
        //         gestao.getModelLabel(),
        //         labels
        // );

    }

}
