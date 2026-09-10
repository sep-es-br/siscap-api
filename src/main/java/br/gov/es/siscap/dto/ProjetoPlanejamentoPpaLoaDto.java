package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoPlanejamentoPpaLoa;

public record ProjetoPlanejamentoPpaLoaDto(
    Long id,
    Long projetoId,
    String codAcao,
    String codFuncao,
    String codPrograma,
    String ano,
    String codUo
) {

    public ProjetoPlanejamentoPpaLoaDto(ProjetoPlanejamentoPpaLoa entity) {
        this(
            entity.getId(),
            entity.getProjeto().getId(),
            entity.getCodAcao(),
            entity.getCodFuncao(),
            entity.getCodPrograma(),
            entity.getAno(),
            entity.getCodUo()
        );
    }

}

