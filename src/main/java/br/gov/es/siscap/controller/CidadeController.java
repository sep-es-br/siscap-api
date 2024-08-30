package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.CidadeMicrorregiaoSelectDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.enums.FiltroCidade;
import br.gov.es.siscap.service.CidadeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cidades")
@RequiredArgsConstructor
public class CidadeController {

    private final CidadeService service;

    @GetMapping("/select")
    public List<SelectDto> listarSelect(@NotNull @RequestParam FiltroCidade filtrarPor, @RequestParam Long id) {
        return service.buscarSelect(filtrarPor, id);
    }

    @GetMapping("/select/microrregioes")
    public List<CidadeMicrorregiaoSelectDto> listarSelectComMicrorregioes() {
        return service.buscarSelectComMicrorregioes();
    }
}
