package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.enums.TipoIndicadorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/tipos-indicador")
@RequiredArgsConstructor
public class TipoIndicadorController {
	
	// private final TipoValorService service;
	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoes() {
    return Arrays.stream(TipoIndicadorEnum.values())
        .map(e -> new OpcoesDto( e.getId(), e.getDescricao() ) )
        .toList();
}

}