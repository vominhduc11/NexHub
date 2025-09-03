package com.devwonder.user_service.config;

import com.devwonder.common.config.BaseSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configureServiceEndpoints(AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // Inter-service communication - ONLY allow calls from auth-service with valid API key
            .requestMatchers("/user/reseller", "/user/reseller/**").access(authApiKeyRequired())
            .requestMatchers("/user/admin", "/user/admin/**").access(authApiKeyRequired())
            .requestMatchers("/api/customer").access(authApiKeyRequired())
            
            // Validation endpoints for cross-service calls (warranty-service)
            .requestMatchers("/user/reseller/*/exists", "/api/customer/*/exists").access(gatewayHeaderRequired())
            
            // All other user endpoints - ONLY accessible via API Gateway
            .requestMatchers("/users/**", "/admins/**", "/customers/**", "/resellers/**").access(gatewayHeaderRequired());
    }
}