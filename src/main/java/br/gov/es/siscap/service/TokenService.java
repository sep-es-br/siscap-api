package br.gov.es.siscap.service;

import br.gov.es.siscap.enums.FormatoDataEnum;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.utils.FormatadorData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TokenService {

	private static final String ISSUER = "SEP Siscap API";

	@Value("${token.secret}")
	private String secret;

	public String gerarToken(Usuario usuario) {
		try {
			Algorithm algoritmo = Algorithm.HMAC256(secret);
			return JWT.create()
						.withIssuer(ISSUER)
						.withSubject(usuario.getSub())
						.withExpiresAt(getDataExpiracao())
						.sign(algoritmo);
		} catch (JWTCreationException exception) {
			throw new SiscapServiceException(List.of("Erro ao gerar o token", exception.getMessage()));
		}
	}

	public String validarToken(String token) {
		Algorithm algoritmo = Algorithm.HMAC256(secret);
		return JWT.require(algoritmo)
					.withIssuer(ISSUER)
					.build()
					.verify(token)
					.getSubject();
	}

	public String buscarDataExpiracaoToken(String token) {
		LocalDateTime dataExpiracao = LocalDateTime.ofInstant(JWT.decode(token).getExpiresAt().toInstant(), ZoneOffset.of("-03:00"));
		return FormatadorData.formatar(dataExpiracao, FormatoDataEnum.COMPLETO);
	}

	private Instant getDataExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}

}