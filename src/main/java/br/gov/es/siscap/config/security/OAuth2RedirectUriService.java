package br.gov.es.siscap.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class OAuth2RedirectUriService {

    @Value("${oauth2.redirect.frontend.projetos}")
    private String uriProjetos;

    @Value("${oauth2.redirect.frontend.programas}")
    private String uriProgramas;

    public String resolverRedirectUri(
            String returnUrl,
            String redirectUriPadrao) {

        if (returnUrl == null || returnUrl.isBlank()) {
            return redirectUriPadrao;
        }

        String path;

        try {
            
            URI uri = URI.create(returnUrl);

            if (uri.isAbsolute()
                    || uri.getRawAuthority() != null) {
                return redirectUriPadrao;
            }

            path = uri.getPath();

        } catch (IllegalArgumentException e) {
            return redirectUriPadrao;
        }

        if (path.equals("/main/projetos/editar")
                || path.matches(
                    "^/main/projetos/editar/[0-9]+$")) {

            return uriProjetos;
        }

        if (path.equals("/main/programas/editar")
                || path.matches(
                    "^/main/programas/editar/[0-9]+$")) {

            return uriProgramas;
        }

        return redirectUriPadrao;
    }
}
