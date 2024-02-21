package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.EntidadeSelectDto;
import br.gov.es.siscap.service.EntidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entidades")
@RequiredArgsConstructor
public class EntidadeController {

    private final EntidadeService service;

    @GetMapping("/select")
    public List<EntidadeSelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
