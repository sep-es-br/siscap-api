package br.gov.es.siscap.enums.edocs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EtapasIntegracaoEdocsEnum {

    CAPTURAASSINA(1),
    AUTUAR(2),
    ENTRANHARARQUIVO(3),
    DESPACHARPROCESSO(4);

    private final int value;

}
