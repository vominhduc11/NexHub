package com.devwonder.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication and authorization service")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DevWonder Team")
                                .email("support@devwonder.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Via API Gateway"),
                        new Server().url("http://localhost:8081").description("Direct access (dev only)")
                ));
    }
}