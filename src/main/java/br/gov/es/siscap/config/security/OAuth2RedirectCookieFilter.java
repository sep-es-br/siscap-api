package br.gov.es.siscap.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OAuth2RedirectCookieFilter
        extends OncePerRequestFilter {

    public static final String RESPONSE_ATTRIBUTE =
            OAuth2RedirectCookieFilter.class.getName()
            + ".RESPONSE";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        request.setAttribute(RESPONSE_ATTRIBUTE, response);

        try {
            filterChain.doFilter(request, response);
        } finally {
            request.removeAttribute(RESPONSE_ATTRIBUTE);
        }
    }
    
}
