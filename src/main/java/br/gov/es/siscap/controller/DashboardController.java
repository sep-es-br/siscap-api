package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.DashboardDadosDto;
import br.gov.es.siscap.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 12/02/2025
// ALTERACOES PROVISORIAS APENAS PARA APRESENTACAO; A SEREM REMOVIDAS POSTERIORMENTE
@Tag(name = "Dashboard", description = "Endpoints para consulta de dados consolidados, indicadores e informações gerenciais exibidas no dashboard do sistema.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

	private final DashboardService service;

	@GetMapping
	public ResponseEntity<DashboardDadosDto> buscarDadosDashboard() {
		return ResponseEntity.ok(service.buscarDadosDashboard());
	}
}
