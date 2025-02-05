package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaOrganizacaoInfoEssencialDto;
import br.gov.es.siscap.dto.organogramawebapi.OrganogramaUnidadeInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "organogramaWeb", url = "${api.organograma.uri.webapi}")
public interface OrganogramaWebClient {

	@GetMapping("/organizacoes/{guidGOVES}/filhas")
	List<OrganogramaOrganizacaoInfoEssencialDto> buscarOrganizacoesFilhasGOVES(@RequestHeader Map<String, Object> headers, @PathVariable String guidGOVES);

	@GetMapping("/organizacoes/{guid}")
	OrganogramaOrganizacaoDto buscarOrganizacaoPorGuid(@RequestHeader Map<String, Object> headers, @PathVariable String guid);

	@GetMapping("/unidades/{guid}/info")
	OrganogramaUnidadeInfoDto buscarUnidadeInfoPorGuid(@RequestHeader Map<String, Object> headers, @PathVariable String guid);
}
