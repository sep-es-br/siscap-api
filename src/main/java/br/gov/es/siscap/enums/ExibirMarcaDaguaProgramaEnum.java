package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExibirMarcaDaguaProgramaEnum {

    EXIBIR("S"),
    NAOEXIBIR("N");
    
    private final String value;

}
