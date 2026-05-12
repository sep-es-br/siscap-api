package br.gov.es.siscap.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api.parecer.overrides")
public class OverrideProperties {

    private Map<String, String> lotacaoUsuario = new HashMap<>();

}
