package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RetornoAssinaturaEdocsDto(

        @JsonProperty("capturado") 
        Boolean capturado,

        @JsonProperty("idCapturaEvento") 
        String idCapturaEvento

) {
}
