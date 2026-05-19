package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.service.PlanoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Planos", description = "Endpoints para consulta de planos disponíveis.")
@RestController
@RequestMapping("/planos")
@RequiredArgsConstructor
public class PlanoController {

	private final PlanoService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}