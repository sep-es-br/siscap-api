package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
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

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}
}