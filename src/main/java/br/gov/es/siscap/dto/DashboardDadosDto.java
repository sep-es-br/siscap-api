package br.gov.es.siscap.dto;

import java.math.BigDecimal;

// 12/02/2025
// ALTERACOES PROVISORIAS APENAS PARA APRESENTACAO; A SEREM REMOVIDAS POSTERIORMENTE

public record DashboardDadosDto(

			Integer projetosQuantidade,
			BigDecimal projetosValorTotal,
			Integer programasQuantidade,
			Integer cartasConsultaQuantidade
) {
}


