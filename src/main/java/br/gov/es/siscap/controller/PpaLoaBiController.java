package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.AcaoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPeriodoPpaLoaDto;
import br.gov.es.siscap.dto.indicadoresexternos.OpcoesPpaLoaDto;
import br.gov.es.siscap.service.PpaLoaBiService;
import feign.Param;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

	@GetMapping("/ppa/anos")
	public List<OpcoesPpaLoaDto> listarAnosPeriodoPpaAtivo() {
		return service.listarAnosPpaAtivo();
	}

	@GetMapping("/ppa/uos/{ppa}")
	public List<OpcoesPpaLoaDto> listarUosPorPpa(@PathVariable @NotNull String ppa) {
		return service.listarUosAnoPpa(ppa);
	}

	@GetMapping("/ppa/funcoes")
	public List<OpcoesPpaLoaDto> listarFuncoes(
        @RequestParam String ppa,
        @RequestParam List<Long> uos) {
		return service.listarFuncoes( ppa, uos );
	}

	@GetMapping("/ppa/programas")
	public List<OpcoesPpaLoaDto> listarProgramas(
        @RequestParam String ppa,
        @RequestParam List<Long> uos,
		@RequestParam List<Long> funcoes ) {
		return service.listarProgramas( ppa, uos, funcoes );
	}

	@GetMapping("/ppa/acoes")
	public List<OpcoesPpaLoaDto> listarAcoes(
		@RequestParam String ppa,
		@RequestParam List<Long> uos,
		@RequestParam List<Long> funcoes,
		@RequestParam List<Long> programas ) {
		return service.listarAcoes( funcoes, programas, ppa, uos );
	}

	@GetMapping("/ppa/acoes/dados")
	public List<AcaoPpaLoaDto> dadosAcoes( 
		@RequestParam(required = false) String ppa,
		@RequestParam List<Long> funcoes,
		@RequestParam List<Long> programas,
		@RequestParam List<Long> anos,
		@RequestParam List<Long> uos,
		@RequestParam List<Long> acoes) {
		return service.dadosAcoes( ppa, funcoes, programas, anos, uos, acoes );
	}

}