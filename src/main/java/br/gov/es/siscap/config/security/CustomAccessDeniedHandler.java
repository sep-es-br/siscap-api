package br.gov.es.siscap.config.security;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import br.gov.es.siscap.infra.MensagemErroRest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LogManager.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.warn("SECURITY_ACCESS_DENIED method={} uri={} authenticated={} authorities={}",
                request.getMethod(), request.getRequestURI(),
                authentication != null && authentication.isAuthenticated(),
                authentication == null ? List.of() : authentication.getAuthorities());
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(ToStringBuilder
                .reflectionToString(new MensagemErroRest(HttpStatus.FORBIDDEN,
                        "Usuário sem permissão.",
                        List.of("Recurso não permitido para o seu nível de usuário.")), ToStringStyle.JSON_STYLE));
    }
}
