package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "organogramaWeb", url = "${api.organograma.uri.webapi}")
public interface OrganogramaWebClient {

	@GetMapping("/organizacoes/{guid}")
	OrganogramaOrganizacaoDto buscarOrganizacaoPorGuid(@RequestHeader Map<String, Object> headers, @PathVariable String guid);
}
