package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.TipoOrganizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-organizacoes")
@RequiredArgsConstructor
public class TipoOrganizacaoController {

	private final TipoOrganizacaoService service;

	@GetMapping("/select")
	public List<SelectDto> listarSelect() {
		return service.buscarSelect();
	}
}
