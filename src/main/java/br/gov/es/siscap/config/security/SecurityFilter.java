package br.gov.es.siscap.config.security;

import br.gov.es.siscap.infra.MensagemErroRest;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import br.gov.es.siscap.service.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

	private static final Logger logger = LogManager.getLogger(SecurityFilter.class);

	private final Roles roles;
	private final TokenService tokenService;
	private final UsuarioRepository usuarioRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getRequestURI().endsWith("/user-info")) {
			filterChain.doFilter(request, response);
			return;
		}
		String authorizationHeader = request.getHeader("Authorization");
		var token = recuperarToken(request);
		boolean authorizationPresent = authorizationHeader != null && !authorizationHeader.isBlank();
		boolean bearerFormat = authorizationPresent && authorizationHeader.startsWith("Bearer ")
				&& authorizationHeader.length() > "Bearer ".length();

		logger.info("SECURITY_START method={} uri={} authorizationPresent={} bearerFormat={}",
				request.getMethod(), request.getRequestURI(), authorizationPresent, bearerFormat);

		if (token != null) {
			Usuario usuario;
			try {
				String subNovo = tokenService.validarToken(token);
				usuario = (Usuario) usuarioRepository.findBySub(subNovo);

				if (usuario == null) {
					logger.warn("SECURITY_USER_NOT_FOUND subHash={} method={} uri={}",
							hashIdentifier(subNovo), request.getMethod(), request.getRequestURI());
					enviarMensagemErro(
							List.of("Usuário não encontrado. Faça o login novamente"), response);
					return;
				}

				logger.info("SECURITY_TOKEN_VALID subHash={} roles={}",
						hashIdentifier(subNovo), usuario.getPapeis());
			} catch (JWTVerificationException e) {
				logger.warn("SECURITY_TOKEN_INVALID reason={} method={} uri={}",
						e.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());
				List<String> erros = new ArrayList<>();
				erros.add(tratarExcecaoToken(e, token));
				enviarMensagemErro(erros, response);
				return;
			}


			Set<SimpleGrantedAuthority> authorities = new HashSet<>();
			usuario.getPapeis().forEach(papel -> authorities.addAll(roles.getAuthorities(papel)));
			logger.info("SECURITY_AUTHORITIES roles={} authorities={}",
					usuario.getPapeis(), authorities.stream()
							.map(SimpleGrantedAuthority::getAuthority).sorted().toList());

			var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(autenticacao);
		} else {
			logger.warn("SECURITY_NO_TOKEN method={} uri={}", request.getMethod(), request.getRequestURI());
		}

		filterChain.doFilter(request, response);
	}

	private String recuperarToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if (authHeader == null) return null;
		return authHeader.replace("Bearer ", "");
	}

	private void enviarMensagemErro(List<String> erros, HttpServletResponse response) throws IOException {
		String mensagem = ToStringBuilder.reflectionToString(
					new MensagemErroRest(UNAUTHORIZED, "Token Invalido", erros), ToStringStyle.JSON_STYLE);
		response.setHeader("Content-Type", "application/json");
		response.setStatus(UNAUTHORIZED.value());
		response.getWriter().write(mensagem);
	}

	private String tratarExcecaoToken(JWTVerificationException e, String token) {
		return switch (e.getClass().getSimpleName()) {
			case "JWTDecodeException" -> "Houve um erro ao decodificar o token.";
			case "TokenExpiredException" -> "Token expirado em " + tokenService.buscarDataExpiracaoToken(token);
			default -> "Por favor, faça o login novamente.";
		};
	}

	private String hashIdentifier(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(value.getBytes(StandardCharsets.UTF_8));
			return java.util.HexFormat.of().formatHex(digest).substring(0, 12);
		} catch (Exception e) {
			return "unavailable";
		}
	}
}
