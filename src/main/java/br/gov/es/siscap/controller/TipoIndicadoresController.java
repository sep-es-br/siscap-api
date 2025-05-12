package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-indicadores")
@RequiredArgsConstructor
public class TipoIndicadoresController {
	//private final TipoPapelService service;
	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return null;
		// return //service.listarOpcoesDropdown();
	}
}