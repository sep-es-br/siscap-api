package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoOds;

public record ProjetoOdsDto(
        Integer idOdsProjeto,
        Integer odsId,
        Integer odsOrdem,
        String odsNome,
        String odsDescricao) {

    public ProjetoOdsDto(ProjetoOds entity) {
        this(
            entity.getId(),
            entity.getIdOds(),
            null,
            "",
            ""
        );
    }

    public ProjetoOdsDto(Integer odsId, Integer odsOrdem, String nomeOds, String descricaoOds) {
        this(
            null,
            odsId,
            odsOrdem,
            nomeOds,
            descricaoOds
        );
    }
}
