package br.gov.es.siscap.config;

import br.gov.es.siscap.infra.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class SiscapConfig {

    private final String authoritiesFile;

    public SiscapConfig(@Value("${authorities.file}") String authoritiesFile) {
        this.authoritiesFile = authoritiesFile;
    }

    @Bean
    Roles roles() throws IOException {
        return new ObjectMapper().readValue(new File(authoritiesFile), Roles.class);
    }

}
