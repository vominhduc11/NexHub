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
            // Test endpoints - Allow direct access for testing (HIGH PRIORITY)
            .requestMatchers("/notification/test/**").permitAll()
            
            // Health check endpoint - Allow direct access (HIGH PRIORITY)
            .requestMatchers("/notifications/health").permitAll()
            
            // WebSocket endpoints - Allow both direct and Gateway access (HIGHEST PRIORITY)
            .requestMatchers("/ws/**", "/websocket/**", "/info", "/sockjs-node/**").permitAll()
            
            // SockJS specific endpoints
            .requestMatchers("/**/websocket", "/**/info", "/**/iframe.html").permitAll()
            
            // All other notification endpoints - Block REST endpoints since we're using pure WebSocket
            .requestMatchers("/notifications/**").denyAll();
    }
}