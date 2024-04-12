package br.gov.es.siscap.config.security;

import br.gov.es.siscap.infra.MensagemErroRest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.getWriter().write(ToStringBuilder
                .reflectionToString(new MensagemErroRest(HttpStatus.FORBIDDEN,
                        "Usuário sem permissão.",
                        List.of("Recuso não permitido para o seu nível de usuário.")), ToStringStyle.JSON_STYLE));
    }
}
