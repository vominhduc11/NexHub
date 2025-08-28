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
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(
            // Authentication interceptor
            new ChannelInterceptor() {
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("WebSocket CONNECT attempt - checking authentication");
                    
                    // Get JWT token from Authorization header (forwarded from API Gateway)
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    log.info("Authorization header: {}", authHeader != null ? "Bearer ***" : "null");
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        
                        // Check if token is expired
                        if (JwtUtil.isTokenExpired(token)) {
                            log.warn("JWT token is expired");
                            return null; // Reject connection
                        }
                        
                        // Extract username from JWT token
                        String username = JwtUtil.extractUsername(token);
                        String userType = JwtUtil.extractUserType(token);
                        
                        log.info("Extracted user info - username: {}, userType: {}", username, userType);
                        
                        if (username != null && !username.isEmpty()) {
                            // Extract and log user roles
                            java.util.List<String> userRoles = JwtUtil.extractRoles(token);
                            log.info("User roles from JWT: {}", userRoles);
                            
                            // Check if user has ADMIN role for WebSocket access
                            if (!JwtUtil.hasAnyRole(token, new String[]{"ADMIN"})) {
                                log.warn("WebSocket access denied for user: {} - ADMIN role required. User roles: {}", username, userRoles);
                                return null; // Reject connection
                            }
                            
                            log.info("WebSocket authentication successful for ADMIN user: {} ({})", username, userType);
                            
                            // Store JWT token in session for authorization checks
                            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                            if (sessionAttributes != null) {
                                sessionAttributes.put("jwtToken", token);
                                sessionAttributes.put("userRoles", JwtUtil.extractRoles(token));
                                sessionAttributes.put("userPermissions", JwtUtil.extractPermissions(token));
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