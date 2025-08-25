package com.devwonder.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Actuator health check (always allow for Docker health checks)
                .requestMatchers("/actuator/health").permitAll()
                
                // Swagger docs (ONLY via API Gateway)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header
                ))
                
                // Product list endpoints - permitAll via API Gateway
                .requestMatchers(HttpMethod.GET, "/product/products", "/product/products/category/**", "/product/products/search").access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header (permitAll for public access)
                ))
                
                // Validation endpoints for cross-service calls (warranty-service)
                .requestMatchers("/api/product-serials/*/exists").access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"
                ))
                
                // Product creation endpoint - require authentication via API Gateway (JWT validated at Gateway)
                .requestMatchers(HttpMethod.POST, "/product/products").access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header (JWT + ADMIN role validated at Gateway)
                ))
                
                // Category endpoints - allow via API Gateway
                .requestMatchers("/product/categories/**").access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header
                ))
                
                // Other product endpoints - require authentication via API Gateway  
                .requestMatchers("/product/**").access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header
                ))
                
                // Block all other direct access
                .anyRequest().denyAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}