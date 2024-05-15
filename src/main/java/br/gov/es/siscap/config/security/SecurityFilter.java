package br.gov.es.siscap.config.security;

import br.gov.es.siscap.infra.MensagemErroRest;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import br.gov.es.siscap.service.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final Roles roles;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().endsWith("/user-info")) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = recuperarToken(request);
        if (token != null) {
            Usuario usuario;
            try {
                String subNovo = tokenService.validarToken(token);
                usuario = (Usuario) usuarioRepository.findBySubNovo(subNovo);

                if (usuario == null) {
                    enviarMensagemErro(UNAUTHORIZED,
                            List.of("Usuário não encontrado. Faça o login novamente"), response);
                    return;
                }
            } catch (JWTVerificationException e) {
                var expiresAt = LocalDateTime.ofInstant(JWT.decode(token).getExpiresAt().toInstant(), ZoneOffset.of("-03:00"));
                List<String> erros = new ArrayList<>();
                erros.add("Por favor, faça o login novamente.");
                if (LocalDateTime.now().isAfter(expiresAt))
                    erros.add("Token expirado em " + expiresAt);
                enviarMensagemErro(UNAUTHORIZED, erros, response);
                return;
            }


            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            usuario.getPapeis().forEach(papel -> authorities.addAll(roles.getAuthorities(papel)));

            var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    private void enviarMensagemErro(HttpStatus status, List<String> erros, HttpServletResponse response) throws IOException {
        String mensagem = ToStringBuilder.reflectionToString(
                new MensagemErroRest(status, "Token Invalido", erros), ToStringStyle.JSON_STYLE);
        response.setHeader("Content-Type", "application/json");
        response.setStatus(status.value());
        response.getWriter().write(mensagem);
    }
}
