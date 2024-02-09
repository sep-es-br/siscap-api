package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.MicroregiaoSelectDto;
import br.gov.es.siscap.service.MicroregiaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microregiao")
public class MicroregiaoController {

    private final MicroregiaoService service;

    @GetMapping("/select")
    public List<MicroregiaoSelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
