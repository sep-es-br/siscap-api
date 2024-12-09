package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.LocalidadeOpcoesDto;
import br.gov.es.siscap.service.LocalidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/localidades")
public class LocalidadeController {

	private final LocalidadeService service;

	@GetMapping("/opcoes")
	public List<LocalidadeOpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}