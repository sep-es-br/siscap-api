package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.EixoService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eixos")
@RequiredArgsConstructor
public class EixoController {

	private final EixoService service;

	@GetMapping("/select")
	public List<SelectDto> listarSelect(@NotNull Long idPlano) {
		return service.buscarSelectPorPlano(idPlano);
	}
}
