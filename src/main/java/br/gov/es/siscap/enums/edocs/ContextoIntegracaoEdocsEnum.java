package br.gov.es.siscap.enums.edocs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContextoIntegracaoEdocsEnum {

    DIC(1),
    PROGRAMA(2);
    
    private final int value;

}
