package br.gov.es.siscap.controller;


import br.gov.es.siscap.dto.ObjetoSelectDto;
import br.gov.es.siscap.service.ObjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/objetos")
@RequiredArgsConstructor
public class ObjetoController {

	private final ObjetoService service;

	@GetMapping("/select")
	public List<ObjetoSelectDto> listarObjetosSelect() {
		return service.buscarSelect();
	}
}
