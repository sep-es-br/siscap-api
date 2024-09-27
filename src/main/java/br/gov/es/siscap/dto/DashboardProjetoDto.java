package br.gov.es.siscap.dto;

import java.math.BigDecimal;

public record DashboardProjetoDto(

			Integer quantidade,
			BigDecimal valorTotal
) {
}
