package com.devwonder.api_gateway.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Swagger UI
                        .pathMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/webjars/**",
                            "/v3/api-docs/**",
                            "/api/*/v3/api-docs/**",
                            "/api/*/swagger-ui/**",
                            "/api/*/webjars/**",
                            "/api/user/v3/api-docs/**",
                            "/api/user/swagger-ui/**",
                            "/api/user/webjars/**",
                            "/auth/swagger-ui.html",
                            "/auth/swagger-ui/**",
                            "/auth/webjars/**",
                            "/auth/v3/api-docs"
                        ).permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        // Product endpoints - public access (all product endpoints for testing)
                        .pathMatchers("/api/product/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri("http://auth-service:8081/auth/.well-known/jwks.json")
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> rolesList = (java.util.List<String>) rolesObj;
                return rolesList.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(java.util.stream.Collectors.toList());
            }
            // Default role if no roles found
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        });
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }
}
