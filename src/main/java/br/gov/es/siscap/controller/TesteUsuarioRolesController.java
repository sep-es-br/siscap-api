package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaUnidadeInfoDto;
import br.gov.es.siscap.service.TesteUsuarioRolesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test-user-roles")
@RequiredArgsConstructor
public class TesteUsuarioRolesController {

	private final TesteUsuarioRolesService service;

	@GetMapping("/papeis/{sub}")
	public List<ACAgentePublicoPapelDto> listarPapeisAgentePublicoPorSub(@PathVariable String sub) {
		return service.listarPapeisAgentePublicoPorSub(sub);
	}

	@GetMapping("/unidade-info/{lotacaoGuid}")
	public OrganogramaUnidadeInfoDto listarUnidadeInfoPorLotacaoGuid(@PathVariable String lotacaoGuid) {
		return service.listarUnidadeInfoPorLotacaoGuid(lotacaoGuid);
	}

	@GetMapping("/organizacao/{guid}")
	public OrganogramaOrganizacaoDto listarDadosOrganizacaoPorGuid(@PathVariable String guid) {
		return service.listarDadosOrganizacaoPorGuid(guid);
	}

}
