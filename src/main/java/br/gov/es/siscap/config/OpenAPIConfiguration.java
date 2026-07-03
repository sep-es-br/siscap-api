package br.gov.es.siscap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${openapi.server.url}")
    private String absoluteUrl;

    @Value("${openapi.external-docs.url}")
    private String externalDocsUrl;

    @Bean
    public OpenAPI defineOpenApi() {
        return new OpenAPI()
                .info(info())
                .servers(List.of(server()))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .externalDocs(new ExternalDocumentation()
                        .description("Captação de Recursos - Governo do Estado do Espírito Santo")
                        .url(externalDocsUrl))
                .tags(
                        List.of(
                            new Tag().name("DIC").description("Endpoints para manipulação dos DIC´s.")
                                
                        )
                );
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
                    responses.addApiResponse("401",
                            new ApiResponse().description("Token JWT inválido, expirado ou ausente"));
                    responses.addApiResponse("403",
                            new ApiResponse().description("Usuário não possui permissão para acessar este recurso"));
                    responses.addApiResponse("500",
                            new ApiResponse().description("Erro interno inesperado no servidor"));
                }));
    }

    private Server server() {
        Server server = new Server();
        server.url(absoluteUrl);
        server.description("SISCAP API");
        return server;
    }

    private Contact contact() {
        Contact contact = new Contact();
        contact.name("SISCAP - SEP");
        contact.url("");
        return contact;
    }

    private Info info() {
        return new Info()
                .title("Captação de Recursos API")
                .version("1.0.0")
                .description("SISCAP SEP")
                .contact(contact());
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
