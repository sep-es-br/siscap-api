package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.service.IndicadorExternoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping("/catalogo-externo")
@RequiredArgsConstructor
public class IndicadorCatalogoController {

	private final IndicadorExternoService service;

	@GetMapping("/gestoes")
	public List<OpcoesGestaoIndicadorDto> listarGestoesAtivasIndicadores() {
		return service.listarGestoesAtivasIndicadores();
	}

	@GetMapping("/gestoes/{idGestao}/indicadores")
	public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(@PathVariable @NotNull Long idGestao,
			@RequestParam(required = false) List<Long> label,
			@RequestParam(required = false) List<Long> lableValor,
			@RequestParam(required = false) List<Long> desafio) {
		return service.listarIndicadoresFiltro(idGestao,
				label,
				lableValor,
				desafio);
	}

}