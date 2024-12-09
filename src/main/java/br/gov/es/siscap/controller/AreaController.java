package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.service.AreaService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaController {

	private final AreaService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown(@NotNull Long idEixo) {
		return service.listarOpcoesDropdown(idEixo);
	}
}