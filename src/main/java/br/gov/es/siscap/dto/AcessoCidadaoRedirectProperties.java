package br.gov.es.siscap.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acesso-cidadao.redirect-uri")
public record AcessoCidadaoRedirectProperties(
        String padrao,
        String projeto,
        String programa) {
}