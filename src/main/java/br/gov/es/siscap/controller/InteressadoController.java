package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.InteressadoOpcoesDto;
import br.gov.es.siscap.service.InteressadoService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping("/interessados")
@RequiredArgsConstructor
public class InteressadoController {

	private final InteressadoService service;

	@GetMapping("/opcoes")
	public List<InteressadoOpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}