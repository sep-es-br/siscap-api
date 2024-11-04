package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.enums.FiltroCidade;
import br.gov.es.siscap.service.CidadeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cidades")
@RequiredArgsConstructor
public class CidadeController {

	private final CidadeService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown(@NotNull @RequestParam FiltroCidade filtrarPor, @RequestParam Long id) {
		return service.listarOpcoesDropdown(filtrarPor, id);
	}
}