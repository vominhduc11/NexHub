package com.devwonder.user_service.config;

import com.devwonder.common.config.BaseSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configureServiceEndpoints(AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // Validation endpoints for cross-service calls (warranty-service)
            .requestMatchers("/user/reseller/*/exists", "/user/admin/*/exists", "/api/customer/*/exists").access(gatewayHeaderRequired())
            
            // API endpoints accessible via Gateway (with authentication)
            .requestMatchers(HttpMethod.GET, "/user/reseller").access(gatewayHeaderRequired())
            .requestMatchers(HttpMethod.DELETE, "/user/reseller/*").access(gatewayHeaderRequired())
            
            // POST endpoints for creating resellers - inter-service call
            .requestMatchers(HttpMethod.POST, "/user/reseller").access(authApiKeyRequired())
            
            // Inter-service communication - ONLY allow calls from auth-service with valid API key
            .requestMatchers("/user/admin", "/api/customer").access(authApiKeyRequired());
    }
}