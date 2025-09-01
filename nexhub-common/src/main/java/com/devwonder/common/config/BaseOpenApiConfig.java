package com.devwonder.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;

public abstract class BaseOpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${spring.application.name:nexhub-service}")
    private String serviceName;
    
    protected abstract String getServiceDescription();
    protected abstract String getServiceVersion();
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(getServiceTitle())
                .description(getServiceDescription())
                .version(getServiceVersion())
                .contact(new Contact()
                    .name("DevWonder Team")
                    .email("support@devwonder.com")
                    .url("https://devwonder.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(getServers())
            .addSecurityItem(new SecurityRequirement()
                .addList("JWT")
                .addList("Gateway-Header"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for authentication"))
                .addSecuritySchemes("Gateway-Header", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-Gateway-Request")
                    .description("Gateway authentication header")));
    }
    
    private String getServiceTitle() {
        String[] words = serviceName.replace("-", " ").split(" ");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                title.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1).toLowerCase())
                     .append(" ");
            }
        }
        return title.toString().trim() + " API";
    }
    
    private List<Server> getServers() {
        return List.of(
            new Server()
                .url("http://localhost:8080")
                .description("API Gateway (Production)"),
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Direct Service Access (Development)")
        );
    }
}