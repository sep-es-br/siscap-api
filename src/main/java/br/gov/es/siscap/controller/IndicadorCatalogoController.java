package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
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

	@GetMapping("/gestao")
	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores() {
		return service.listarGestoesAtivasIndicadores();
	}

	@GetMapping("/indicadores")
	public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(
			@RequestParam(required = false) Long filtroGestao,
			@RequestParam(required = false) List<Long> filtroLabel,
			@RequestParam(required = false) List<Long> filtroLableValor,
			@RequestParam(required = false) List<Long> filtroDesafio) {
		return service.listarIndicadoresFiltro(filtroGestao,
				filtroLabel,
				filtroLableValor,
				filtroDesafio);
	}

}