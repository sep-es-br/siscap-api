package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.acessocidadaoapi.ACAgentePublicoPapelDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.acessocidadaoapi.SubResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "acessoCidadaoWeb", url = "${api.acessocidadao.uri.webapi}")
public interface AcessoCidadaoWebClient {

	@PutMapping("/api/cidadao/{cpf}/pesquisaSub")
	SubResponseDto buscarSubPorCpf(@RequestHeader Map<String, Object> headers, @PathVariable String cpf);

	@GetMapping("/api/agentepublico/{sub}")
	AgentePublicoACDto.AgentePublicoACResponseDto buscarAgentePublicoPorSub(@RequestHeader Map<String, Object> headers,
	                                                                        @PathVariable String sub);

	@GetMapping("/api/agentepublico/{sub}/papeis")
	List<ACAgentePublicoPapelDto> buscarPapeisAgentePublicoPorSub(@RequestHeader Map<String, Object> headers, @PathVariable String sub);
}
