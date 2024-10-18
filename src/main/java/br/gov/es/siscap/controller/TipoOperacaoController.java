package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.TipoOperacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-operacao")
@RequiredArgsConstructor
public class TipoOperacaoController {

	private final TipoOperacaoService service;

	@GetMapping("/select")
	public List<SelectDto> listarTipoOperacaoSelect() {
		return service.buscarSelect();
	}
}
