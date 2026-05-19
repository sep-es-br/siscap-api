package br.gov.es.siscap.controller;


import br.gov.es.siscap.dto.opcoes.ObjetoOpcoesDto;
import br.gov.es.siscap.service.ObjetoService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/objetos")
@RequiredArgsConstructor
public class ObjetoController {

	private final ObjetoService service;

	@GetMapping("/opcoes")
	public List<ObjetoOpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}