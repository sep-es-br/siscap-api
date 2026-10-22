package br.gov.es.siscap.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegatedRequestResolver;
    private final OAuth2RedirectStateService redirectStateService;

    private final OAuth2RedirectUriService redirectUriService;
    private static final Logger logger = LogManager.getLogger(AuthorizationRequestResolver.class);
    

    public AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            String authorizeUri,
            OAuth2RedirectStateService redirectStateService,
            OAuth2RedirectUriService redirectUriService) {

        this.delegatedRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, authorizeUri);

        this.redirectStateService = redirectStateService;

        this.redirectUriService = redirectUriService;

    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request) {

        OAuth2AuthorizationRequest req = delegatedRequestResolver.resolve(request);

        return customizeRequest(req, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request,
            String clientRegistrationId) {

        OAuth2AuthorizationRequest req = delegatedRequestResolver.resolve(
                request, clientRegistrationId);

        return customizeRequest(req, request);
    }

    private OAuth2AuthorizationRequest customizeRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request) {

        if (authorizationRequest == null) {
            return null;
        }

        String returnUrl = request.getParameter("returnUrl");

        String redirectUri = redirectUriService.resolverRedirectUri(
                returnUrl,
                authorizationRequest.getRedirectUri());

        // Mantém o contexto de retorno associado ao state.
        if (returnUrl != null && !returnUrl.isBlank()) {

            Object attribute = request.getAttribute(
                    OAuth2RedirectCookieFilter.RESPONSE_ATTRIBUTE);

            if (attribute instanceof HttpServletResponse response) {

                redirectStateService.criarCookie(
                        request,
                        response,
                        authorizationRequest.getState(),
                        returnUrl);
            }
        }

        Map<String, Object> params = new HashMap<>(
                authorizationRequest.getAdditionalParameters());

        // Preserva a integração atual do Acesso Cidadão.
        params.put(
                OAuth2ParameterNames.RESPONSE_TYPE,
                authorizationRequest.getResponseType().getValue()
                        + " id_token token");

        logger.info(
                "[OAUTH2] Callback selecionado: {}",
                redirectUri);

        return OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .redirectUri(redirectUri)
                .additionalParameters(params)
                .build();
    }

}
