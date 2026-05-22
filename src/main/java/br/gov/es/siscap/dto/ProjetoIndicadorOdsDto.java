package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoIndicadorOds;

public record ProjetoIndicadorOdsDto(

    Integer id,
    Integer idOdsIndicadorExterno,
    Integer idOdsExterno,
    Integer odsId,
    Integer odsOrdem,
    String odsDescricao,
    Integer idIndicadorExterno
) {

public ProjetoIndicadorOdsDto(ProjetoIndicadorOds projetoIndicadorOds) {
    this(
            projetoIndicadorOds.getId(),
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getId()
                    : null,
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    && projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno().getId()
                    : null,
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    && projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno().getOdsId()
                    : null,
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    && projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno().getOdsOrdem()
                    : null,
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    && projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getOdsExterno().getOdsDescricao()
                    : null,
            projetoIndicadorOds.getOdsIndicadorExterno() != null
                    && projetoIndicadorOds.getOdsIndicadorExterno().getIndicadorExterno() != null
                    ? projetoIndicadorOds.getOdsIndicadorExterno().getIndicadorExterno().getId()
                    : null
    );
}
}
