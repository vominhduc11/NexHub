package com.devwonder.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexHub API Gateway")
                        .description("API Gateway for NexHub microservices")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local server")
                ));
    }

    @Bean
    public GroupedOpenApi authServiceApi() {
        return GroupedOpenApi.builder()
                .group("auth-service")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationServiceApi() {
        return GroupedOpenApi.builder()
                .group("notification-service")
                .pathsToMatch("/api/notification/**")
                .build();
    }

    @Bean
    public GroupedOpenApi blogServiceApi() {
        return GroupedOpenApi.builder()
                .group("blog-service")
                .pathsToMatch("/api/blog/**")
                .build();
    }

    @Bean
    public GroupedOpenApi warrantyServiceApi() {
        return GroupedOpenApi.builder()
                .group("warranty-service")
                .pathsToMatch("/api/warranty/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productServiceApi() {
        return GroupedOpenApi.builder()
                .group("product-service")
                .pathsToMatch("/api/product/**")
                .build();
    }

    @Bean
    public GroupedOpenApi languageServiceApi() {
        return GroupedOpenApi.builder()
                .group("language-service")
                .pathsToMatch("/api/language/**")
                .build();
    }
}