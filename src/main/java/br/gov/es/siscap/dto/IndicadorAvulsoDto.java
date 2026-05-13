package br.gov.es.siscap.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record IndicadorAvulsoDto(
    Integer id,

    @Size(max = 2000)
    String nomeIndicador,

    @Size(max = 255)
    String unidadeMedida,

    @Size(max = 500)
    String fonteIndicador,

    @Size(max = 255)
    String medidoPor,

    @Size(max = 255)
    String baseDeReferencia,

    @Valid
    List<IndicadorAvulsoMetaDto> metasGlobais

) {

}
