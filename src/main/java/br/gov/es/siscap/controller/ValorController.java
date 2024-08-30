package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.ValorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/valor")
@RequiredArgsConstructor
public class ValorController {

	private final ValorService service;

	@GetMapping("/select")
	public List<SelectDto> listarValoresSelect() {
		return service.buscarSelect();
	}
}
