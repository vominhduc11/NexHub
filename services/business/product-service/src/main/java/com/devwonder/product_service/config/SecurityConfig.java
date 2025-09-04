package com.devwonder.product_service.config;

import com.devwonder.common.config.BaseSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configureServiceEndpoints(AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // Product list endpoints - permitAll via API Gateway
            .requestMatchers(HttpMethod.GET, "/product/products", "/product/products/category/**", "/product/products/search").access(gatewayHeaderRequired())
            
            // Validation endpoints for cross-service calls (warranty-service)
            .requestMatchers("/api/product-serials/*/exists").access(gatewayHeaderRequired())
            
            // Product creation endpoint - require authentication via API Gateway
            .requestMatchers(HttpMethod.POST, "/product/products").access(gatewayHeaderRequired())
            
            // Category endpoints - allow via API Gateway
            .requestMatchers("/product/categories/**").access(gatewayHeaderRequired())
            
            // Other product endpoints - require authentication via API Gateway  
            .requestMatchers("/product/**").access(gatewayHeaderRequired());
    }
}