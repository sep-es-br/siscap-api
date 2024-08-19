package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.MicrorregiaoCidadesDto;
import br.gov.es.siscap.service.MicrorregiaoCidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/microrregioes-cidades")
@RequiredArgsConstructor
public class MicrorregiaoCidadesController {

	private final MicrorregiaoCidadesService service;

	@GetMapping("/select")
	public List<MicrorregiaoCidadesDto> buscarSelect() {
		return service.buscarSelect();
	}
}
