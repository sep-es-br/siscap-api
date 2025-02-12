package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.DashboardDadosDto;
import br.gov.es.siscap.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 12/02/2025
// ALTERACOES PROVISORIAS APENAS PARA APRESENTACAO; A SEREM REMOVIDAS POSTERIORMENTE

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
