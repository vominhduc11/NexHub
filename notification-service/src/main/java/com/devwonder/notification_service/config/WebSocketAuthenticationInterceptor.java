package com.devwonder.notification_service.config;

import com.devwonder.notification_service.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {
    
    private static final String BEARER_PREFIX = "Bearer ";
    
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.debug("WebSocket authentication interceptor - preSend called");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            log.debug("WebSocket authentication interceptor - command: {}", accessor.getCommand());
        }
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            return handleConnect(accessor, message);
        }
        
        return message;
    }
    
    private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
        log.info("🔌 WebSocket CONNECT attempt");
        
        // Get JWT token from Authorization header
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("❌ No Authorization header or invalid format");
            return null; // Reject connection
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        log.info("🎫 Token received: {}", token.substring(0, Math.min(20, token.length())) + "...");
        
        // Validate JWT token structure
        if (token.isEmpty() || token.split("\\.").length != 3) {
            log.warn("❌ Invalid token format - must have 3 parts");
            return null; // Reject connection
        }
        
        // Validate JWT token (structure + expiration)
        if (!JwtUtil.validateToken(token, jwtSecret)) {
            log.warn("❌ JWT token validation failed (invalid or expired)");
            return null; // Reject connection
        }
        
        // Extract username for logging
        String username = JwtUtil.extractUsername(token);
        if (username == null || username.trim().isEmpty()) {
            log.warn("❌ Invalid username in JWT token");
            return null; // Reject connection
        }
        
        log.info("✅ JWT token valid and not expired - User: '{}' - Connection allowed", username);
        return message; // Allow connection to continue
    }
    
}