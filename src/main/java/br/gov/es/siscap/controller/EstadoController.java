package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.service.EstadoService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estados")
public class EstadoController {

	private final EstadoService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown(@NotNull @RequestParam Long idPais) {
		return service.listarOpcoesDropdown(idPais);
	}
}