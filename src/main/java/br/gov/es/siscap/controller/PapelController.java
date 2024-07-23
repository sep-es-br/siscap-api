package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.PapelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/papeis")
@RequiredArgsConstructor
public class PapelController {

	private final PapelService service;

	@GetMapping("/select")
	public List<SelectDto> listarPapeisSelect() { return service.buscarSelect();}
}
