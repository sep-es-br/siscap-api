package br.gov.es.siscap.dto;

import br.gov.es.siscap.models.ProjetoPlanejamentoPpaLoa;

public record ProjetoPlanejamentoPpaLoaResponseDto(
    Long id,
    Long projetoId,
    String codAcao,
    String codFuncao,
    String codPrograma,
    String ano,
    String codUo,
    AcaoPpaLoaDto acaoPpaLoa
) {

    public ProjetoPlanejamentoPpaLoaResponseDto(ProjetoPlanejamentoPpaLoa entity, AcaoPpaLoaDto acaoPpaLoa) {
        this(
            entity.getId(),
            entity.getProjeto().getId(),
            entity.getCodAcao(),
            entity.getCodFuncao(),
            entity.getCodPrograma(),
            entity.getAno(),
            entity.getCodUo(),
            acaoPpaLoa
        );
    }

    ProjetoPlanejamentoPpaLoaResponseDto(ProjetoPlanejamentoPpaLoa entity) {
        this(
            entity.getId(),
            entity.getProjeto().getId(),
            entity.getCodAcao(),
            entity.getCodFuncao(),
            entity.getCodPrograma(),
            entity.getAno(),
            entity.getCodUo(),
            null
        );
    }

    public ProjetoPlanejamentoPpaLoaResponseDto(AcaoPpaLoaDto acaoPpaLoaDtos) {
        this(
            acaoPpaLoaDtos.id(),
            null,
            acaoPpaLoaDtos.codigoAcao(),
            acaoPpaLoaDtos.codigoFuncao(),
            acaoPpaLoaDtos.codigoPrograma(),
            acaoPpaLoaDtos.anoAcao(),
            acaoPpaLoaDtos.codigoUnidadeOrcamentaria(),
            acaoPpaLoaDtos
        );
    }

}


