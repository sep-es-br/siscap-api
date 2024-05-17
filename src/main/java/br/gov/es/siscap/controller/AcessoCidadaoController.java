package br.gov.es.siscap.controller;

import br.gov.es.siscap.service.AcessoCidadaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AcessoCidadaoController {

    private final AcessoCidadaoService service;

    @GetMapping("/teste")
    public void teste() {
        service.buscarPessoaPorCpf("14976414701");
    }

}
