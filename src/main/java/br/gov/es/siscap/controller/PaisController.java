package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.service.PaisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paises")
@RequiredArgsConstructor
public class PaisController {

    private final PaisService service;

    @GetMapping("/select")
    public List<SelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
