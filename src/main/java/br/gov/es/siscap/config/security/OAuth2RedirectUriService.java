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

    @Value("${oauth2.redirect.frontend.pessoas}")
    private String uriPessoas;

    @Value("${oauth2.redirect.frontend.pesquisa-fontes-financiamento}")
    private String uriPesquisaFontesFinanciamento;

    @Value("${oauth2.redirect.frontend.prospeccoes}")
    private String uriProspeccoes;

    @Value("${oauth2.redirect.frontend.organizacoes}")
    private String uriOrganizacoes;

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

        if (path.equals("/main/pessoas/editar")
                || path.matches(
                    "^/main/pessoas/editar/[0-9]+$")) {

            return uriPessoas;
        }

        if (path.equals("/main/pesquisa-fontes-financiamento/editar")
                || path.matches(
                    "^/main/pesquisa-fontes-financiamento/editar/[0-9]+$")) {

            return uriPesquisaFontesFinanciamento;
        }

        if (path.equals("/main/prospeccoes/editar")
                || path.matches(
                    "^/main/prospeccoes/editar/[0-9]+$")) {

            return uriProspeccoes;
        }

        if (path.equals("/main/organizacoes/editar")
                || path.matches(
                    "^/main/organizacoes/editar/[0-9]+$")) {

            return uriOrganizacoes;
        }

        return redirectUriPadrao;

    }
}
