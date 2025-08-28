package com.devwonder.notification_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import com.devwonder.notification_service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Public broadcasts to /topic
        // Private messages to /queue (user-specific)
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")  // Allow all origins for testing
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(
            // Authentication interceptor
            new ChannelInterceptor() {
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                log.info("WebSocket interceptor - preSend called");
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null) {
                    log.info("WebSocket interceptor - accessor found, command: {}", accessor.getCommand());
                }
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("WebSocket CONNECT attempt - checking authentication");
                    
                    // Get JWT token from Authorization header (forwarded from API Gateway)
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    log.info("Authorization header: {}", authHeader != null ? "Bearer ***" : "null");
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        log.info("Token extracted, length: {}", token.length());
                        
                        // Check if token is expired
                        boolean isExpired = JwtUtil.isTokenExpired(token);
                        log.info("Token expiration check result: {}", isExpired);
                        if (isExpired) {
                            log.warn("JWT token is expired");
                            return null; // Reject connection
                        }
                        
                        // Extract username from JWT token
                        String username = JwtUtil.extractUsername(token);
                        String userType = JwtUtil.extractUserType(token);
                        log.info("JWT extraction results - username: {}, userType: {}", username, userType);
                        
                        log.info("Extracted user info - username: {}, userType: {}", username, userType);
                        
                        if (username != null && !username.isEmpty()) {
                            log.info("WebSocket authentication successful for user: {} ({})", username, userType);
                            
                            // Store JWT token in session
                            Map<String, Object> sessionAttributes = (Map<String, Object>) accessor.getSessionAttributes();
                            if (sessionAttributes != null) {
                                sessionAttributes.put("jwtToken", token);
                            }
                            
                            // Create principal with username from JWT
                            Principal principal = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                            accessor.setUser(principal);
                        } else {
                            log.warn("Failed to extract username from JWT token");
                            return null; // Reject connection
                        }
                    } else {
                        log.warn("No valid JWT token provided for WebSocket connection");
                        return null; // Reject connection - JWT token required
                    }
                }
                
                return message;
            }
            },
            // Authorization interceptor
            new WebSocketAuthorizationInterceptor()
        );
    }
}