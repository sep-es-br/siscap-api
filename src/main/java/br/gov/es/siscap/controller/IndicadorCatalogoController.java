package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.indicadoresexternos.FiltroIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesGestaoIndicadorDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesIndicadoresDto;
import br.gov.es.siscap.form.IndicadorAvulsoForm;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.service.IndicadorExternoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

	@PostMapping("/gestoes/{idGestao}/indicadores")
	public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(@PathVariable @NotNull Long idGestao,
		@RequestBody(required = false) FiltroIndicadorDto filtro) {
		return service.listarIndicadoresFiltro(idGestao, filtro);
	}

	@PostMapping
	public ResponseEntity<List<OpcoesIndicadoresDto>> cadastrarIndicadorAvulso(@Valid @RequestBody IndicadorAvulsoForm form) {
		return new ResponseEntity<>(service.cadastrarIndicadorAvulso(form), HttpStatus.CREATED);
	}

}