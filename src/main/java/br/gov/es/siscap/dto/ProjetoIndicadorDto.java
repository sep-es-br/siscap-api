package br.gov.es.siscap.dto;

import java.util.List;

import br.gov.es.siscap.models.ProjetoIndicador;

public record ProjetoIndicadorDto(
    Integer idIndicador,
    String tipoIndicador,
    String descricaoIndicador,
    String descricaoMeta,
    Long idStatus,
    Integer idIndicadorExterno,
    List<ProjetoIndicadorCatalogoMetaDto> metasIndicadorProjeto
) {
    
    public ProjetoIndicadorDto(ProjetoIndicador projetoIndicador) {
        this(
            projetoIndicador.getId(),
            projetoIndicador.getTipoIndicador(),
            projetoIndicador.getDescricaoIndicador(),
            projetoIndicador.getDescricaoMeta(),
            projetoIndicador.getTipoStatus() != null ? projetoIndicador.getTipoStatus().getId() : null,
            projetoIndicador.getIdIndicadorExterno(),
            projetoIndicador.getMetas() != null
                ? projetoIndicador.getMetas().stream()
                    .map(ProjetoIndicadorCatalogoMetaDto::new)
                    .toList()
                : List.of()
        );
    }

    public ProjetoIndicadorDto(
        ProjetoIndicador projetoIndicador,
        List<ProjetoIndicadorCatalogoMetaDto> metasIndicadorCatalogo
    ) {
        this(
            projetoIndicador.getId(),
            projetoIndicador.getTipoIndicador(),
            projetoIndicador.getDescricaoIndicador(),
            projetoIndicador.getDescricaoMeta(),
            projetoIndicador.getTipoStatus() != null ? projetoIndicador.getTipoStatus().getId() : null,
            projetoIndicador.getIdIndicadorExterno(),
            metasIndicadorCatalogo != null ? metasIndicadorCatalogo : List.of()
        );
    }
}