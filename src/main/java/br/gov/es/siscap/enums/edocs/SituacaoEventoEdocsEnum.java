package br.gov.es.siscap.enums.edocs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SituacaoEventoEdocsEnum {

    CRIADO("Criado"),
    ENFILEIRADO("Enfileirado"),
    PROCESSANDO("Processando"),
    EXECUTADO("Executado"),
    CONCLUIDO("Concluido"),
    CANCELADO("Cancelado");

    private final String value;

}
