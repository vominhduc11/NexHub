package com.devwonder.auth_service.config;

import com.devwonder.common.config.BaseSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configureServiceEndpoints(AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // Actuator endpoints (extended for Eureka)
            .requestMatchers("/actuator/**").permitAll()
            
            // Eureka client endpoints (internal communication)
            .requestMatchers("/eureka/**").permitAll()
            .requestMatchers("/info").permitAll()
            
            // JWKS endpoint (publicly accessible for JWT validation)
            .requestMatchers("/auth/.well-known/jwks.json").permitAll()
            
            // All auth endpoints - ONLY accessible via API Gateway
            .requestMatchers("/auth/**").access(gatewayHeaderRequired());
    }
}