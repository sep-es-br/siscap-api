package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.MicrorregiaoSelectDto;
import br.gov.es.siscap.service.MicrorregiaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microrregiao")
public class MicrorregiaoController {

    private final MicrorregiaoService service;

    @GetMapping("/select")
    public List<MicrorregiaoSelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
