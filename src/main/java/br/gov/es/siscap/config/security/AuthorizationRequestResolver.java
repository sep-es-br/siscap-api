package br.gov.es.siscap.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver delegatedRequestResolver;
    private final Logger logger = LogManager.getLogger(AuthorizationRequestResolver.class);

    public AuthorizationRequestResolver(final ClientRegistrationRepository clientRegistrationRepository,
                                        final String authorizeUri) {
        this.delegatedRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                authorizeUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(final HttpServletRequest request) {
        final OAuth2AuthorizationRequest req = this.delegatedRequestResolver.resolve(request);
        return this.customizeRequest(req);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            final HttpServletRequest request,
            final String clientRegistrationId
    ) {
        final OAuth2AuthorizationRequest req = this.delegatedRequestResolver.resolve(request, clientRegistrationId);
        return this.customizeRequest(req);
    }

    private OAuth2AuthorizationRequest customizeRequest(final OAuth2AuthorizationRequest request) {
        if (request != null) {
            return OAuth2AuthorizationRequest.from(request).additionalParameters(this.additionalParams(request)).build();
        }

        return null;
    }

    private Map<String, Object> additionalParams(final OAuth2AuthorizationRequest request) {
        logger.info("Nova requisição de autenticação para o Acesso Cidadão.");
        final Map<String, Object> params = new HashMap<>(request.getAdditionalParameters());
        params.put(OAuth2ParameterNames.RESPONSE_TYPE, request.getResponseType().getValue() + " id_token token");
        return params;
    }
}
