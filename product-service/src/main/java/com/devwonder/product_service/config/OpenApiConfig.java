package com.devwonder.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String JWT_SECURITY_NAME = "JWT Authentication";
    private static final String GATEWAY_HEADER_NAME = "Gateway Request";

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("Product catalog management service")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DevWonder Team")
                                .email("support@devwonder.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Via API Gateway"),
                        new Server().url("http://localhost:8084").description("Direct access (dev only)")
                ))
                .components(new Components()
                        .addSecuritySchemes(JWT_SECURITY_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token"))
                        .addSecuritySchemes(GATEWAY_HEADER_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Gateway-Request")
                                .description("Gateway request header (required for all endpoints)")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(GATEWAY_HEADER_NAME))
                .addSecurityItem(new SecurityRequirement()
                        .addList(JWT_SECURITY_NAME));
    }
}