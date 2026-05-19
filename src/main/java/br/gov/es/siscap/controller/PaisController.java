package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.service.PaisService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/paises")
@RequiredArgsConstructor
public class PaisController {

	private final PaisService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}