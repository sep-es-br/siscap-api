package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.AreaAtuacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/areas-atuacao")
@RequiredArgsConstructor
public class AreaAtuacaoController {

	private final AreaAtuacaoService service;

	@GetMapping("/select")
	public List<SelectDto> listarSelect() {
		return service.buscarSelect();
	}
}
