package br.gov.es.siscap.controller;

import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AutenticacaoController {

	@Value("${frontend.host}")
	private String frontEndHost;

	private final AutenticacaoService service;

	/**
	 * Endpoint necessário para fazer o redirecionamento do token de acesso para o front end.
	 * Este endpoint recebe o redirecionamento a partir do arquivo acesso-cidadao-response.html que se fez necessário
	 *
	 * @param accessToken O access token do acesso cidadão.
	 * @return Um redirect para o frontend passando o token codigicado.
	 */
	@Hidden
	@GetMapping("/acesso-cidadao-response")
	public RedirectView acessoCidadaoResponse(String accessToken) {
		String tokenEmBase64 = Base64.getEncoder().encodeToString(accessToken.getBytes());
		return new RedirectView(String.format("%s/token?token=%s", frontEndHost, tokenEmBase64));
	}

	@GetMapping("/user-info")
	public UsuarioDto montarUsuarioDto(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return service.autenticar(authorization.replace("Bearer ", ""));
	}
}
