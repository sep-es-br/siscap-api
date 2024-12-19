package br.gov.es.siscap.service;

import br.gov.es.siscap.client.AcessoCidadaoTokenClient;
import br.gov.es.siscap.dto.acessocidadaoapi.LoginACResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcessoCidadaoAutorizacaoService {

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";

	@Value("${api.acessocidadao.client-id}")
	private String clientId;
	@Value("${api.acessocidadao.client-secret}")
	private String clientSecret;
	@Value("${api.acessocidadao.grant_type}")
	private String grantType;
	@Value("${api.acessocidadao.scope}")
	private String scope;

	private final AcessoCidadaoTokenClient ACTokenClient;

	public HashMap<String, Object> getAuthorizationHeader() {
		return buildAuthorizationHeader();
	}

	public HashMap<String, Object> getAccessTokenAuthorizationHeader(String token) {
		return buildAuthHeaderHashMap(token);
	}

	private HashMap<String, Object> buildAuthorizationHeader() {
		final String basicToken = clientId + ":" + clientSecret;

		Map<String, Object> basicTokenHeaders = buildBasicTokenHeaders(basicToken);
		String basicTokenForm = buildBasicTokenForm();

		LoginACResponseDto loginACResponseDto = ACTokenClient.login(basicTokenHeaders, basicTokenForm);

		return buildAuthHeaderHashMap(loginACResponseDto.accessToken());
	}

	private Map<String, Object> buildBasicTokenHeaders(String basicToken) {
		Map<String, Object> basicTokenHeaders = new HashMap<>();

		basicTokenHeaders.put(AUTHORIZATION, "Basic " + Base64.getEncoder()
					.encodeToString(basicToken.getBytes(StandardCharsets.UTF_8)));
		basicTokenHeaders.put("Content-Type", "application/x-www-form-urlencoded");

		return basicTokenHeaders;
	}

	private String buildBasicTokenForm() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("grant_type", grantType);
		parameters.put("scope", scope);

		return parameters.entrySet()
					.stream()
					.map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
					.collect(Collectors.joining("&"));
	}

	private HashMap<String, Object> buildAuthHeaderHashMap(String token) {
		HashMap<String, Object> authorizationHeader = new HashMap<>();
		authorizationHeader.put(AUTHORIZATION, BEARER + token);
		return authorizationHeader;
	}
}
