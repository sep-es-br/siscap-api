package br.gov.es.siscap.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.LinkedHashMap;
import java.util.Map;

@FeignClient(name = "acessoCidadaoUserInfo", url = "${api.acessocidadao.uri.userinfo}")
public interface AcessoCidadaoUserInfoClient {

	@GetMapping
	LinkedHashMap<String, Object> buscarUserInfoAcessoCidadao(@RequestHeader Map<String, Object> headers);
}
