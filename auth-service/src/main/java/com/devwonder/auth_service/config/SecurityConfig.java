package com.devwonder.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // CORS preflight requests - MUST be first
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Actuator endpoints (always allow for Docker health checks and Eureka)
                .requestMatchers("/actuator/**").permitAll()
                
                // Eureka client endpoints (internal communication)
                .requestMatchers("/eureka/**").permitAll()
                .requestMatchers("/info").permitAll()
                
                // JWKS endpoint (publicly accessible for JWT validation)
                .requestMatchers("/auth/.well-known/jwks.json").permitAll()
                
                // Swagger docs (ONLY via API Gateway)
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).access(new WebExpressionAuthorizationManager(
                    "request.getHeader('X-Gateway-Request') == 'true'"   // ONLY Gateway header
                ))
                
                // All auth endpoints - ONLY accessible via API Gateway
                .requestMatchers("/auth/**").access(new WebExpressionAuthorizationManager(
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