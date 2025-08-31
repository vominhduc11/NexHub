package com.devwonder.notification_service.config;

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
        // Get roles from session attributes (stored during JWT validation)
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) accessor.getSessionAttributes().get("roles");
        
        if (roles != null && !roles.isEmpty()) {
            log.debug("Found roles in session attributes: {}", roles);
            return roles;
        }
        
        // Try to get userType from session attributes as fallback
        String userType = (String) accessor.getSessionAttributes().get("userType");
        if (userType != null && !userType.trim().isEmpty()) {
            log.debug("Using userType as role: {}", userType);
            return List.of(userType);
        }
        
        // Default to empty list if no roles found
        log.warn("No roles found in session attributes for user");
        return List.of();
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
               // Common customer username patterns
               lowerUsername.matches(".*customer.*") ||
               // Default: if not clearly admin/dealer, assume customer
               (!lowerUsername.contains("admin") && !lowerUsername.contains("dealer"));
    }

    private boolean hasPermissionForDestination(String destination, List<String> userRoles, String username) {
        // Check if user has ADMIN role (either in roles list or userType)
        boolean isAdmin = userRoles.contains("ADMIN") || userRoles.contains("admin");
        
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