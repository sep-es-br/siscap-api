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

	@GetMapping("/ppa/uos/{ano}")
	public List<OpcoesPpaLoaDto> listarUosPorAno(@PathVariable @NotNull Long ano) {
		return service.listarUosAnoPpa(ano);
	}

	@GetMapping("/ppa/funcoes")
	public List<OpcoesPpaLoaDto> listarFuncoes(
        @RequestParam List<Long> anos,
        @RequestParam List<Long> uos) {
		return service.listarFuncoes( anos, uos );
	}

	@GetMapping("/ppa/programas")
	public List<OpcoesPpaLoaDto> listarProgramas(
        @RequestParam List<Long> anos,
        @RequestParam List<Long> uos,
		@RequestParam List<Long> funcoes ) {
		return service.listarProgramas( anos, uos, funcoes );
	}

	@GetMapping("/ppa/acoes")
	public List<OpcoesPpaLoaDto> listarAcoes(
		@RequestParam List<Long> funcoes,
		@RequestParam List<Long> programas,
		@RequestParam List<Long> anos,
		@RequestParam List<Long> uos) {
		return service.listarAcoes( funcoes, programas, anos, uos );
	}

	@GetMapping("/ppa/acoes/dados")
	public List<AcaoPpaLoaDto> dadosAcoes(
		@RequestParam List<Long> funcoes,
		@RequestParam List<Long> programas,
		@RequestParam List<Long> anos,
		@RequestParam List<Long> uos,
		@RequestParam List<Long> acoes) {
		return service.dadosAcoes( funcoes, programas, anos, uos, acoes );
	}

	// .set('funcoes', idFuncoes.join(','))
    //   .set('programas', idsProgramas.join(','))
    //   .set('anos', idAnos.join(','))
    //   .set('uos', idUos.join(','));

	// @PostMapping("/gestoes/{idGestao}/indicadores")
	// public List<OpcoesIndicadoresDto> listarIndicadoresFiltro(@PathVariable
	// @NotNull Long idGestao,
	// @RequestBody(required = false) FiltroIndicadorDto filtro) {
	// return service.listarIndicadoresFiltro(idGestao, filtro);
	// }

	// @GetMapping("/ods")
	// public List<OdsPentahoBiDto> listarOds() {
	// return service.listarOdsBI();
	// }

}