package br.gov.es.siscap.dto.indicadoresexternos;

import br.gov.es.siscap.models.OdsIndicadorExterno;

public record OdsIndicadorExternoDto(
    Integer idOdsIndicadorExterno,
    Integer idOdsExterno,
    Integer odsId,
    Integer odsOrdem,
    String odsNome,
    String odsDescricao,
    String odsCor
) {

    // public OdsIndicadorExternoDto( OdsIndicadorExterno entity ) {
    //     this(
    //         entity.getId(),
    //         entity.getOdsExterno().getId(),
    //         entity.getOdsExterno().getOdsId(),
    //         entity.getOdsExterno().getOdsOrdem(),
    //         entity.getOdsExterno().getOdsNome(),
    //         entity.getOdsExterno().getOdsDescricao(),
    //         entity.getOdsExterno().getOdsCor()
    //     );
    // }

}
