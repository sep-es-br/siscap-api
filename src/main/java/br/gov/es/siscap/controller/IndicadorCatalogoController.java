package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.opcoes.OpcoesDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.service.OrganizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/indicadores")
@RequiredArgsConstructor
public class IndicadorCatalogoController {

	private final OrganizacaoService service;

	@GetMapping("/opcoes")
	public List<OpcoesDto> listarOpcoesDropdown(
				@RequestParam(required = false) Long filtroTipoOrganizacao
	) {
		return service.listarOpcoesDropdown(filtroTipoOrganizacao);
	}

}