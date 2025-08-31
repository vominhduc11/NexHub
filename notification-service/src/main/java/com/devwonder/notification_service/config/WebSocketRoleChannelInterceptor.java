package com.devwonder.notification_service.config;

import com.devwonder.notification_service.service.JwtService;
import com.devwonder.common.exception.AuthenticationException;
import com.devwonder.common.exception.AuthorizationException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Component
public class WebSocketRoleChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketRoleChannelInterceptor.class);
    
    private final JwtService jwtService;
    
    @Autowired
    public WebSocketRoleChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Principal user = accessor.getUser();
            
            if (destination != null && user != null) {
                String username = user.getName();
                List<String> userRoles = getUserRoles(accessor);
                
                log.info("WebSocket SEND command - User: {}, Destination: {}, Roles: {}", 
                         username, destination, userRoles);
                
                if (!hasPermissionForDestination(destination, userRoles, username)) {
                    log.error("ACCESS DENIED - User: {} with roles: {} cannot access destination: {}", 
                            username, userRoles, destination);
                    throw new AccessDeniedException("Access denied to destination: " + destination);
                }
                
                log.info("ACCESS GRANTED - User: {} authorized for destination: {}", username, destination);
            } else {
                log.warn("Missing destination ({}) or user ({}) in SEND command", destination, user);
            }
        }
        
        return message;
    }

    private List<String> getUserRoles(StompHeaderAccessor accessor) {
        // Extract and validate JWT token for real-time role extraction
        String token = extractTokenFromHeaders(accessor);
        
        if (token == null || token.isEmpty()) {
            log.error("No JWT token found in headers for role extraction");
            throw new AccessDeniedException("Authentication token required");
        }
        
        try {
            // Validate JWT token and extract fresh roles
            JWTClaimsSet claimsSet = jwtService.validateToken(token);
            List<String> roles = jwtService.extractRoles(claimsSet);
            
            if (roles != null && !roles.isEmpty()) {
                log.debug("Extracted roles from JWT token: {}", roles);
                return roles;
            }
            
            // Fallback to userType if no roles found
            String userType = jwtService.extractUserType(claimsSet);
            if (userType != null && !userType.trim().isEmpty()) {
                log.debug("Using userType from JWT as role: {}", userType);
                return List.of(userType);
            }
            
            // No roles or userType found
            log.warn("No roles or userType found in JWT token");
            return List.of();
            
        } catch (AuthenticationException | AuthorizationException e) {
            log.error("JWT token validation failed during role extraction: {}", e.getMessage());
            throw new AccessDeniedException("Invalid or expired token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during JWT token validation: {}", e.getMessage());
            throw new AccessDeniedException("Token validation failed: " + e.getMessage());
        }
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

    private boolean isCustomerUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        String lowerUsername = username.toLowerCase().trim();
        
        // Check if username suggests CUSTOMER role
        return lowerUsername.contains("customer") ||
               lowerUsername.contains("user") ||
               lowerUsername.contains("client") ||
               lowerUsername.startsWith("cust") ||
               // Default: if not clearly admin/dealer, assume customer
               (!lowerUsername.contains("admin") && !lowerUsername.contains("dealer"));
    }

    private boolean hasPermissionForDestination(String destination, List<String> userRoles, String username) {
        // Check if user has ADMIN role
        boolean isAdmin = userRoles.stream()
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(role));
        
        // Broadcast messages - Only ADMIN can send
        if (destination.equals("/app/broadcast")) {
            log.info("Broadcast permission check - User: {}, Roles: {}, Is Admin: {}", username, userRoles, isAdmin);
            return isAdmin;
        }
        
        // Private messages - Only ADMIN can send, only to CUSTOMER
        if (destination.startsWith("/app/private/")) {
            String targetUser = destination.substring("/app/private/".length());
            boolean isTargetCustomer = isCustomerUsername(targetUser);
            
            log.info("Private message permission check - Sender: {}, Roles: {}, Target: {}, Is Admin: {}, Target is Customer: {}", 
                    username, userRoles, targetUser, isAdmin, isTargetCustomer);
            
            // Both conditions must be met:
            // 1. Sender must be ADMIN
            // 2. Target must be CUSTOMER
            boolean hasPermission = isAdmin && isTargetCustomer;
            
            if (!isAdmin) {
                log.warn("Private message denied: Sender {} is not ADMIN (roles: {})", username, userRoles);
            }
            if (!isTargetCustomer) {
                log.warn("Private message denied: Target {} is not identified as CUSTOMER", targetUser);
            }
            
            return hasPermission;
        }
        
        // Default: deny access to unknown destinations
        log.warn("Unknown destination: {} - Access denied", destination);
        return false;
    }
}