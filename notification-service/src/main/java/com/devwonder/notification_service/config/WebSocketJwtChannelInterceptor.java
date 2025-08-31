package com.devwonder.notification_service.config;

import com.devwonder.notification_service.service.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // This is a STOMP CONNECT frame - validate JWT token
            String token = extractTokenFromHeaders(accessor);
            
            if (token == null || token.isEmpty()) {
                log.warn("No JWT token provided in STOMP CONNECT frame");
                throw new RuntimeException("Authentication required");
            }
            
            try {
                // Validate JWT token (includes expiration check)
                JWTClaimsSet claimsSet = jwtService.validateToken(token);
                
                // Check if user has CUSTOMER role
                List<String> roles = jwtService.extractRoles(claimsSet);
                if (roles == null || !roles.contains("CUSTOMER")) {
                    log.warn("STOMP CONNECT denied: User does not have CUSTOMER role. Roles: {}", roles);
                    throw new RuntimeException("Access denied: CUSTOMER role required");
                }
                
                // Create a Principal and set it in the accessor
                Principal principal = createPrincipal(claimsSet);
                accessor.setUser(principal);
                
                // Store additional user info in session attributes
                accessor.getSessionAttributes().put("accountId", jwtService.extractAccountId(claimsSet));
                accessor.getSessionAttributes().put("username", jwtService.extractUsername(claimsSet));
                accessor.getSessionAttributes().put("userType", jwtService.extractUserType(claimsSet));
                accessor.getSessionAttributes().put("roles", roles);
                accessor.getSessionAttributes().put("permissions", jwtService.extractPermissions(claimsSet));
                
                log.info("STOMP CONNECT authenticated for user: {} (ID: {}) with roles: {}", 
                        jwtService.extractUsername(claimsSet), 
                        jwtService.extractAccountId(claimsSet),
                        roles);
                
            } catch (Exception e) {
                log.error("STOMP CONNECT authentication failed: {}", e.getMessage());
                throw new RuntimeException("Authentication failed: " + e.getMessage());
            }
        }
        
        return message;
    }

    private String extractTokenFromHeaders(StompHeaderAccessor accessor) {
        // Try to get token from Authorization header
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        
        // Try to get token from custom header
        List<String> tokenHeaders = accessor.getNativeHeader("token");
        if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
            return tokenHeaders.get(0);
        }
        
        return null;
    }

    private Principal createPrincipal(JWTClaimsSet claimsSet) {
        String username = jwtService.extractUsername(claimsSet);
        Long accountId = jwtService.extractAccountId(claimsSet);
        
        return new Principal() {
            @Override
            public String getName() {
                return username != null ? username : String.valueOf(accountId);
            }
        };
    }
}