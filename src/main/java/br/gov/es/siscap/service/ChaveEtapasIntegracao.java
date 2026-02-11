package br.gov.es.siscap.service;

import br.gov.es.siscap.enums.edocs.ContextoIntegracaoEdocsEnum;

public record ChaveEtapasIntegracao(
    Long id,
    ContextoIntegracaoEdocsEnum tipo) 
    {
}
