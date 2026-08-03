package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPeriodoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPpaLoaDto;
import br.gov.es.siscap.service.PpaLoaBiService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ppaloa/bi")
@RequiredArgsConstructor
public class PpaLoaBiController {

	private final PpaLoaBiService service;

	@GetMapping("/ppa")
	public OpcoesPeriodoPpaLoaDto listarGestoesAtivasIndicadores() {
		return service.listarPeriodoPpaAtivo();
	}

	@GetMapping("/ppa")
	public List<OpcoesPpaLoaDto> listarAnosPeriodoPpaAtivo() {
		return service.AnosPpaAtivo();
	}

	// @PostMapping("/gestoes/{idGestao}/indicadores")
	// public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(@PathVariable @NotNull Long idGestao,
	// 	@RequestBody(required = false) FiltroIndicadorDto filtro) {
	// 	return service.listarIndicadoresFiltro(idGestao, filtro);
	// }

	// @GetMapping("/ods")
	// public List<OdsPentahoBiDto> listarOds() {
	// 	return service.listarOdsBI();
	// }

}