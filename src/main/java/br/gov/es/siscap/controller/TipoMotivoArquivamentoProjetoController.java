package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.MotivosArquivamentoOpcoesDto;
import br.gov.es.siscap.service.TipoMotivoArquivamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-arquivamento-projeto")
@RequiredArgsConstructor
public class TipoMotivoArquivamentoProjetoController {

	private final TipoMotivoArquivamentoService service;

	@GetMapping("/opcoes")
	public List<MotivosArquivamentoOpcoesDto> listarOpcoesDropdown() {
		return service.listarOpcoesDropdown();
	}

}