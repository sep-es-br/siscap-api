package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RetornoAssinaturaEdocsDto(

        @JsonProperty("capturado") 
        boolean capturado,

        @JsonProperty("idCapturaEvento") 
        String idCapturaEvento

) {
}
