package br.gov.es.siscap.controller;

import br.gov.es.siscap.config.security.OAuth2RedirectStateService;
import br.gov.es.siscap.config.security.OAuth2RedirectUriService;
import br.gov.es.siscap.config.security.SecurityFilter;
import br.gov.es.siscap.dto.UsuarioDto;
import br.gov.es.siscap.service.AcessoCidadaoAutorizacaoService;
import br.gov.es.siscap.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.web.bind.annotation.PostMapping;
import java.nio.charset.StandardCharsets;

@Hidden
@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class AutenticacaoController {

	@Value("${frontend.host}")
	private String frontEndHost;

	private final AutenticacaoService service;
	private final AcessoCidadaoAutorizacaoService autorizacaoACService;

	private final OAuth2RedirectStateService redirectStateService;
	private final OAuth2RedirectUriService redirectUriService;
	private static final Logger logger = LogManager.getLogger(AutenticacaoController.class);

	/**
	 * Endpoint necessário para fazer o redirecionamento do token de acesso para o
	 * front end.
	 * Este endpoint recebe o redirecionamento a partir do arquivo
	 * acesso-cidadao-response.html que se fez necessário
	 *
	 * @param accessToken O access token do acesso cidadão.
	 * @return Um redirect para o frontend passando o token codigicado.
	 */

	@Hidden
	@GetMapping("/acesso-cidadao-response")
	public RedirectView acessoCidadaoResponse(
			@RequestParam String accessToken,
			@RequestParam(required = false) String state,
			HttpServletRequest request,
			HttpServletResponse response) {

		// Recupera o destino associado ao state recebido.
		String returnUrl = redirectStateService
				.recuperarRota(request, state)
				.orElse(null);

		// Descarta o cookie de correlação.
		redirectStateService.limparCookie(request, response);

		// Preserva o processamento existente do token.
		DecodedJWT jwt = JWT.decode(accessToken);

		Map<String, Claim> claims = jwt.getClaims();

		String subNovo = claims.get("subNovo").asString();

		long expSeconds = jwt.getExpiresAt().toInstant().getEpochSecond()
				- Instant.now().getEpochSecond();

		autorizacaoACService.storeEdocsToken(
				subNovo, accessToken, expSeconds);

		String tokenEmBase64 = Base64.getEncoder()
				.encodeToString(accessToken.getBytes(
						java.nio.charset.StandardCharsets.UTF_8));

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(frontEndHost)
				.path("/token")
				.queryParam("token", tokenEmBase64);

		if (returnUrl != null) {
			uriBuilder.queryParam("returnUrl", returnUrl);
		}

		return new RedirectView(
				uriBuilder.build().encode().toUriString());
	}

	// @Hidden
	// @GetMapping("/acesso-cidadao-response")
	// public RedirectView acessoCidadaoResponse(String accessToken) {
	// DecodedJWT jwt = JWT.decode(accessToken);
	// Map<String, Claim> claims = jwt.getClaims();
	// String subNovo = claims.get("subNovo").asString();
	// long expSeconds = jwt.getExpiresAt().toInstant().getEpochSecond() -
	// Instant.now().getEpochSecond();
	// autorizacaoACService.storeEdocsToken(subNovo, accessToken, expSeconds);
	// String tokenEmBase64 =
	// Base64.getEncoder().encodeToString(accessToken.getBytes());
	// return new RedirectView(String.format("%s/token?token=%s", frontEndHost,
	// tokenEmBase64));
	// }

	@GetMapping("/user-info")
	public UsuarioDto montarUsuarioDto(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return service.autenticar(authorization.replace("Bearer ", ""));
	}

	@PostMapping("/acesso-cidadao-response")
	public RedirectView receberCallbackAngular(
			@RequestParam String accessToken,
			@RequestParam String state,
			HttpServletRequest request,
			HttpServletResponse response) {

		String returnUrl = redirectStateService
				.recuperarRota(request, state)
				.orElse(null);

		logger.info("[OAUTH2] ReturnUrl recuperada: {}", returnUrl);

		// Callback Angular exige correlação válida.
		if (returnUrl == null) {
			redirectStateService.limparCookie(request, response);

			return new RedirectView(frontEndHost + "/login");
		}

		// Confirma que a resposta veio do fluxo que solicitou
		// um callback Angular autorizado.
		String callbackEsperado = redirectUriService.resolverRedirectUri(
				returnUrl, "");

		if (callbackEsperado.isBlank()) {
			redirectStateService.limparCookie(request, response);

			return new RedirectView(frontEndHost + "/login");
		}

		redirectStateService.limparCookie(request, response);

		// Preserva o processamento existente do access token.
		DecodedJWT jwt = JWT.decode(accessToken);

		Map<String, Claim> claims = jwt.getClaims();

		String subNovo = claims.get("subNovo").asString();

		long expSeconds = jwt.getExpiresAt().toInstant().getEpochSecond()
				- Instant.now().getEpochSecond();

		autorizacaoACService.storeEdocsToken(
				subNovo, accessToken, expSeconds);

		String tokenEmBase64 = Base64.getEncoder()
				.encodeToString(
						accessToken.getBytes(StandardCharsets.UTF_8));

		String destino = UriComponentsBuilder
				.fromUriString(frontEndHost)
				.path("/token")
				.queryParam("token", tokenEmBase64)
				.queryParam("returnUrl", returnUrl)
				.build()
				.encode()
				.toUriString();

		logger.info(
				"[OAUTH2] Redirect para frontend com returnUrl: {}",
				returnUrl);

		return new RedirectView(destino);
		
	}

}
