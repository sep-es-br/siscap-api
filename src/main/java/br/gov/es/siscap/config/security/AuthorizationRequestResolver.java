package br.gov.es.siscap.config.security;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import br.gov.es.siscap.dto.AcessoCidadaoRedirectProperties;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationRequestResolver
        implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegatedRequestResolver;

    private final AcessoCidadaoRedirectProperties redirectProperties;

    private final Logger logger =
            LogManager.getLogger(AuthorizationRequestResolver.class);


    public AuthorizationRequestResolver(
            final ClientRegistrationRepository clientRegistrationRepository,
            final String authorizeUri,
            final AcessoCidadaoRedirectProperties redirectProperties) {

        this.delegatedRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        authorizeUri);

        this.redirectProperties = redirectProperties;
    }


    @Override
    public OAuth2AuthorizationRequest resolve(
            final HttpServletRequest request) {

        final OAuth2AuthorizationRequest authorizationRequest =
                this.delegatedRequestResolver.resolve(request);

        return this.customizeRequest(
                authorizationRequest,
                request);
    }


    @Override
    public OAuth2AuthorizationRequest resolve(
            final HttpServletRequest request,
            final String clientRegistrationId) {

        final OAuth2AuthorizationRequest authorizationRequest =
                this.delegatedRequestResolver.resolve(
                        request,
                        clientRegistrationId);

        return this.customizeRequest(
                authorizationRequest,
                request);
    }


    private OAuth2AuthorizationRequest customizeRequest(
            final OAuth2AuthorizationRequest request,
            final HttpServletRequest servletRequest) {

        if (request == null) {
            return null;
        }

        final String redirectUri =
                obterRedirectUri(servletRequest, request);

        logger.info(
                "Nova requisição de autenticação para o Acesso Cidadão. origem={} redirectUri={}",
                servletRequest.getParameter("origem"),
                redirectUri);

        return OAuth2AuthorizationRequest
                .from(request)
                .redirectUri(redirectUri)
                .additionalParameters(this.additionalParams(request))
                .build();
    }


    private String obterRedirectUri(
            final HttpServletRequest servletRequest,
            final OAuth2AuthorizationRequest authorizationRequest) {

        final String origem =
                servletRequest.getParameter("origem");

        if (origem == null || origem.isBlank()) {
            return authorizationRequest.getRedirectUri();
        }

        return switch (origem) {

            case "projeto" ->
                    redirectProperties.projeto();

            case "programa" ->
                    redirectProperties.programa();

            default ->
                    redirectProperties.padrao();
        };
    }


    private Map<String, Object> additionalParams(
            final OAuth2AuthorizationRequest request) {

        final Map<String, Object> params =
                new HashMap<>(request.getAdditionalParameters());

        params.put(
                OAuth2ParameterNames.RESPONSE_TYPE,
                request.getResponseType().getValue()
                        + " id_token token");

        return params;
    }
    
}