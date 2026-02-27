package br.gov.es.siscap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssinanteRequestDto(

		@NotBlank @Size(max = 50) String subAssinante

) {
}