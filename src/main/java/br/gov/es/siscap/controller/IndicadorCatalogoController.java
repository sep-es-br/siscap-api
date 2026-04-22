package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.service.IndicadorExternoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indicadores")
@RequiredArgsConstructor
public class IndicadorCatalogoController {

	private final IndicadorExternoService service;

	// @GetMapping("/opcoes")
	// public List<OpcoesDto> listarOpcoesDropdown(
	// 			@RequestParam(required = false) Long filtroTipoOrganizacao
	// ) {
	// 	return service.listarOpcoesDropdown(filtroTipoOrganizacao);
	// }

	@GetMapping("/gestao")
	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores(
	) {
		return service.listarGestoesAtivasIndicadores();
	}

}