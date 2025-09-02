package com.devwonder.notification_service.config;

import com.devwonder.common.config.BaseSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

    @Override
    protected void configureServiceEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            // Notification API endpoints - Allow access
            .requestMatchers("/notification/**").permitAll()
            
            // Health check endpoint - Allow direct access
            .requestMatchers("/notifications/health").permitAll()
            
            // WebSocket endpoints - Allow both direct and Gateway access
            .requestMatchers("/ws/**", "/websocket/**", "/info", "/sockjs-node/**").permitAll()
            
            // SockJS specific endpoints - Fix invalid patterns
            .requestMatchers("/*/websocket", "/*/info", "/*/iframe.html").permitAll();
    }
}