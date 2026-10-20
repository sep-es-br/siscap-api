package br.gov.es.siscap.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Service
public class OAuth2RedirectStateService {

    private static final String COOKIE_NAME = "SISCAP_OAUTH2_RETURN";
    private static final long TTL_SECONDS = 300;

    private final byte[] secret;

    public OAuth2RedirectStateService(
            @Value("${oauth2.redirect-state-secret}") String secretBase64) {

        this.secret = Base64.getDecoder().decode(secretBase64);

        if (secret.length < 32) {
            throw new IllegalArgumentException(
                "oauth2.redirect-state-secret deve possuir pelo menos 256 bits."
            );
        }
    }

    public void criarCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String state,
            String returnUrl) {

        if (!rotaPermitida(returnUrl)) {
            return;
        }

        long exp = Instant.now().getEpochSecond() + TTL_SECONDS;

        String payload = codificar(state)
                + "." + codificar(returnUrl)
                + "." + exp;

        String assinatura = assinar(payload);

        escreverCookie(
                request,
                response,
                payload + "." + assinatura,
                TTL_SECONDS
        );
    }

    public Optional<String> recuperarRota(
            HttpServletRequest request,
            String stateRecebido) {

        if (stateRecebido == null || stateRecebido.isBlank()) {
            return Optional.empty();
        }

        String cookie = Arrays.stream(
                Optional.ofNullable(request.getCookies())
                    .orElse(new jakarta.servlet.http.Cookie[0])
            )
            .filter(c -> COOKIE_NAME.equals(c.getName()))
            .map(jakarta.servlet.http.Cookie::getValue)
            .findFirst()
            .orElse(null);

        if (cookie == null) {
            return Optional.empty();
        }

        try {
            String[] partes = cookie.split("\\.", -1);

            if (partes.length != 4) {
                return Optional.empty();
            }

            String payload = String.join(
                    ".", partes[0], partes[1], partes[2]);

            byte[] assinaturaRecebida = Base64.getUrlDecoder()
                    .decode(partes[3]);

            byte[] assinaturaEsperada = calcularHmac(payload);

            if (!MessageDigest.isEqual(
                    assinaturaRecebida, assinaturaEsperada)) {
                return Optional.empty();
            }

            long exp = Long.parseLong(partes[2]);

            if (Instant.now().getEpochSecond() >= exp) {
                return Optional.empty();
            }

            String stateOriginal = decodificar(partes[0]);

            if (!MessageDigest.isEqual(
                    stateOriginal.getBytes(StandardCharsets.UTF_8),
                    stateRecebido.getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            String returnUrl = decodificar(partes[1]);

            return rotaPermitida(returnUrl)
                    ? Optional.of(returnUrl)
                    : Optional.empty();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void limparCookie(
            HttpServletRequest request,
            HttpServletResponse response) {

        escreverCookie(request, response, "", 0);
    }

    private void escreverCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String valor,
            long maxAge) {

        String path = request.getContextPath();

        if (path == null || path.isBlank()) {
            path = "/";
        }

        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_NAME, valor)
                .httpOnly(true)
                .secure(request.isSecure())
                .sameSite("Lax")
                .path(path)
                .maxAge(maxAge)
                .build();

        response.addHeader(
                "Set-Cookie", cookie.toString());
    }

    private boolean rotaPermitida(String rota) {

        if (rota == null || rota.isBlank()
                || rota.contains("\\")
                || rota.contains("#")
                || rota.chars().anyMatch(
                    c -> c < 32 || c == 127)) {
            return false;
        }

        try {
            URI uri = new URI(rota);

            if (uri.isAbsolute()
                    || uri.getRawAuthority() != null
                    || uri.getRawFragment() != null) {
                return false;
            }

            String path = uri.getRawPath();

            if (path == null || path.contains("%")
                    || path.contains("//")) {
                return false;
            }

            URI normalizada = uri.normalize();

            if (!path.equals(normalizada.getRawPath())) {
                return false;
            }

            return path.equals("/main")
                    || path.startsWith("/main/")
                    || path.equals("/projetos")
                    || path.startsWith("/projetos/")
                    || path.equals("/home")
                    || path.startsWith("/home/");

        } catch (Exception e) {
            return false;
        }
    }

    private String assinar(String payload) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(calcularHmac(payload));
    }

    private byte[] calcularHmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));

            return mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Falha ao assinar contexto OAuth2.", e);
        }
    }

    private String codificar(String valor) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                    valor.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String valor) {
        return new String(
                Base64.getUrlDecoder().decode(valor),
                StandardCharsets.UTF_8);
    }
    
}
