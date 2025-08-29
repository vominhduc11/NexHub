package com.devwonder.blog_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
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
                
                // All blog endpoints - ONLY accessible via API Gateway
                .requestMatchers("/blogs/**", "/posts/**", "/articles/**").access(new WebExpressionAuthorizationManager(
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