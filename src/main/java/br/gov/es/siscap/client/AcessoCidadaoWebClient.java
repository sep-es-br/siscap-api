package br.gov.es.siscap.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto.AgentePublicoACResponseDto;
import br.gov.es.siscap.dto.acessocidadaoapi.SubResponseDto;

@FeignClient(name = "acessoCidadaoWeb", url = "${api.acessocidadao.uri.webapi}")
public interface AcessoCidadaoWebClient {

	@PutMapping("/api/cidadao/{cpf}/pesquisaSub")
	SubResponseDto buscarSubPorCpf(@RequestHeader Map<String, Object> headers, @PathVariable String cpf);

	@GetMapping("/api/agentepublico/{sub}")
	AgentePublicoACDto.AgentePublicoACResponseDto buscarAgentePublicoPorSub(@RequestHeader Map<String, Object> headers,
	                                                                        @PathVariable String sub);

	@GetMapping("/api/agentepublico/{sub}/papeis")
	List<ACAgentePublicoPapelDto> buscarPapeisAgentePublicoPorSub(@RequestHeader Map<String, Object> headers, @PathVariable String sub);

	@GetMapping("/api/conjunto/{guid}/gestornovo/papel")
	ACAgentePublicoPapelDto buscarGestorNovoConjuntoPorGuidOrganizacao(@RequestHeader Map<String, Object> headers, @PathVariable String guid);

	@GetMapping("/api/conjunto/{guidUnidadeOrganizacao}/agentesPublicos")
	List<AgentePublicoACDto> buscarAgentesPublicosPorGuidUnidade(@RequestHeader Map<String, Object> headers, @PathVariable String guid);

	@GetMapping("/api/conjunto/{guid}/papeis")
	List<ACAgentePublicoPapelDto> buscarAgentesPublicosPapeisPorGuidUnidade(@RequestHeader Map<String, Object> headers, @PathVariable String guid);

	@GetMapping("/api/conjunto/{guid}/gestor")
	AgentePublicoACResponseDto buscarGestorPorGuidUnidade(@RequestHeader Map<String, Object> headers, @PathVariable String guid);

}
