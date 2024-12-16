package br.gov.es.siscap.client;

import br.gov.es.siscap.dto.acessocidadaoapi.ACUserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "acessoCidadaoUserInfo", url = "${api.acessocidadao.uri.userinfo}")
public interface AcessoCidadaoUserInfoClient {

	@GetMapping
	ACUserInfoDto buscarUserInfoAcessoCidadao(@RequestHeader Map<String, Object> headers);
}
