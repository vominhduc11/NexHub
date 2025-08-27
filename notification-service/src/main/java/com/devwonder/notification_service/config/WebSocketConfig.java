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
                .setAllowedOriginPatterns("*")
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
                    // Get JWT token from Authorization header (forwarded from API Gateway)
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        
                        // Check if token is expired
                        if (JwtUtil.isTokenExpired(token)) {
                            log.warn("JWT token is expired");
                            return message; // Reject connection
                        }
                        
                        // Extract username from JWT token
                        String username = JwtUtil.extractUsername(token);
                        String userType = JwtUtil.extractUserType(token);
                        
                        if (username != null && !username.isEmpty()) {
                            log.info("WebSocket authentication successful for user: {} ({})", username, userType);
                            
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
                        }
                    } else {
                        // Fallback: use username header for testing
                        String username = accessor.getFirstNativeHeader("username");
                        if (username != null && !username.isEmpty()) {
                            log.info("WebSocket fallback authentication for user: {}", username);
                            Principal principal = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                            accessor.setUser(principal);
                        }
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