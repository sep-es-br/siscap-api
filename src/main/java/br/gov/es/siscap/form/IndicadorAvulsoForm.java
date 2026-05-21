package br.gov.es.siscap.form;

import br.gov.es.siscap.dto.EquipeDto;
import br.gov.es.siscap.dto.ProjetoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.MetasIndicadorExternoDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record IndicadorAvulsoForm(

		@NotBlank @Size(max = 255) String nomeIndicador,

		@NotBlank @Size(max = 255) String fonteIndicador,

		@NotNull @Positive Long medidoPor,

		@Valid String unidadeMedida,

		@Valid String basedeReferencia,

		@NotEmpty List<MetasIndicadorExternoDto> metasIndicador,

		@NotEmpty List<MetasIndicadorExternoDto> metasIndicadorProjeto

) {
}