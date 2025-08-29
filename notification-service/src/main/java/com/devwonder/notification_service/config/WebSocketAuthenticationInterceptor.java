package com.devwonder.notification_service.config;

import com.devwonder.notification_service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {
    
    private static final String REQUIRED_ROLE = "ADMIN";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String JWT_TOKEN_KEY = "jwtToken";
    private static final String JWT_SECRET = "your-secret-key-here";
    
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.debug("WebSocket authentication interceptor - preSend called");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            log.debug("WebSocket authentication interceptor - command: {}", accessor.getCommand());
        }
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            return handleConnect(accessor);
        }
        
        return message;
    }
    
    private Message<?> handleConnect(StompHeaderAccessor accessor) {
        log.info("WebSocket CONNECT attempt - checking authentication");
        
        // Get JWT token from Authorization header
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        log.debug("Authorization header: {}", authHeader != null ? "Bearer ***" : "null");
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("No valid JWT token provided for WebSocket connection");
            return null; // Reject connection
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        log.debug("Token extracted, length: {}", token.length());
        
        // Validate JWT token (signature + expiration)
        if (!JwtUtil.validateToken(token, JWT_SECRET)) {
            log.warn("JWT token validation failed");
            return null; // Reject connection
        }
        
        // Extract user information from JWT
        String username = JwtUtil.extractUsername(token);
        String userType = JwtUtil.extractUserType(token);
        log.debug("JWT extraction results - username: {}, userType: {}", username, userType);
        
        if (username == null || username.isEmpty()) {
            log.warn("Failed to extract username from JWT token");
            return null; // Reject connection
        }
        
        // Check if user has ADMIN role
        if (!REQUIRED_ROLE.equals(userType)) {
            log.warn("WebSocket access denied - user {} has role {} but {} is required", 
                    username, userType, REQUIRED_ROLE);
            return null; // Reject connection
        }
        
        log.info("WebSocket authentication successful for {} user: {}", REQUIRED_ROLE, username);
        
        // Store JWT token in session for later use
        storeTokenInSession(accessor, token);
        
        // Create principal with username
        Principal principal = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        accessor.setUser(principal);
        
        return null; // Continue processing
    }
    
    private void storeTokenInSession(StompHeaderAccessor accessor, String token) {
        @SuppressWarnings("unchecked")
        Map<String, Object> sessionAttributes = (Map<String, Object>) accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put(JWT_TOKEN_KEY, token);
            log.debug("JWT token stored in session");
        } else {
            log.warn("Session attributes not available - token not stored");
        }
    }
}