package br.gov.es.siscap.dto;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.siscap.models.IndicadorAvulso;
import br.gov.es.siscap.models.IndicadorAvulsoMeta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record IndicadorAvulsoDto(
        Integer id,

        @Size(max = 2000) String nomeIndicador,

        @Size(max = 255) String unidadeMedida,

        @Size(max = 500) String fonteIndicador,

        @Size(max = 255) String medidoPor,

        @Size(max = 255) String baseDeReferencia,

        @Valid List<IndicadorAvulsoMetaDto> metasIndicadorAvulsoGeral

) {

    public IndicadorAvulsoDto(IndicadorAvulso indicadorAvulso) {
        this(
                indicadorAvulso.getId(),
                indicadorAvulso.getNomeIndicador(),
                indicadorAvulso.getUnidadeMedida(),
                indicadorAvulso.getFonteIndicador(),
                indicadorAvulso.getMedidoPor(),
                indicadorAvulso.getBaseDeReferencia(),
                construirMetas(indicadorAvulso));
    }

    private static List<IndicadorAvulsoMetaDto> construirMetas(
            IndicadorAvulso indicadorAvulso) {

        List<IndicadorAvulsoMetaDto> metas = new ArrayList<>();

        if (indicadorAvulso.getMetasIndicadorAvulso() != null) {

            for (IndicadorAvulsoMeta meta : indicadorAvulso.getMetasIndicadorAvulso()) {

                metas.add(
                        new IndicadorAvulsoMetaDto(
                                meta.getId(),
                                meta.getAno(),
                                meta.getValor()));
            }
        }

        return metas;
    }

}
