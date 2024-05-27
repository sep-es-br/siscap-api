package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.DashboardProjetoDto;
import br.gov.es.siscap.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public ResponseEntity<DashboardProjetoDto> quantidadeProjetos() {
        return ResponseEntity.ok(service.buscarDashboardProjetos());
    }

}
