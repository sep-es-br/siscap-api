package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.TipoEntidadeSelectDto;
import br.gov.es.siscap.service.TipoEntidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-entidades")
@RequiredArgsConstructor
public class TipoEntidadeController {

    private final TipoEntidadeService service;

    @GetMapping("/select")
    public List<TipoEntidadeSelectDto> listarSelect() {
        return service.buscarSelect();
    }

}
