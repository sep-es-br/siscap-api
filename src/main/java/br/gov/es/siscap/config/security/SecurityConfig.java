package br.gov.es.siscap.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers(HttpMethod.GET,
                            "/swagger-ui.html",
                            "/swagger-ui/*",
                            "/v3/*",
                            "/v3/api-docs/*",
                            "/signin/acesso-cidadao-response",
                            "/acesso-cidadao-response.html").permitAll();
                    authConfig.anyRequest().authenticated();
                })
                .oauth2Login(oAuth2LoginConfig ->
                    oAuth2LoginConfig
                            .authorizationEndpoint(authEndpointConfig -> authEndpointConfig
                                    .authorizationRequestResolver(new AuthorizationRequestResolver(
                                            clientRegistrationRepository, "/oauth2/authorization")))
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

}
