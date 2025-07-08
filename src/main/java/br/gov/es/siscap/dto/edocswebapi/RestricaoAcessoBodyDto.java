package br.gov.es.siscap.dto.edocswebapi;

import java.util.List;

public record RestricaoAcessoBodyDto(
    Boolean transparenciaAtiva,
    List<String> idsFundamentosLegais,
    ClassificacaoInformacaoDto classificacaoInformacaoDto
) {}
