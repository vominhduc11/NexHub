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

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebSocketAuthorizationInterceptor implements ChannelInterceptor {

    // Define topic permissions
    private static final Map<String, String[]> TOPIC_PERMISSIONS = new HashMap<>();
    
    static {
        // Admin-only topics - all topics now restricted to ADMIN
        TOPIC_PERMISSIONS.put("/topic/dealer-registrations", new String[]{"ADMIN"});
        TOPIC_PERMISSIONS.put("/topic/admin-notifications", new String[]{"ADMIN"});
        TOPIC_PERMISSIONS.put("/topic/dealer-updates", new String[]{"ADMIN"});
        
        // Private queues are handled separately by user principal
    }
    
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Principal user = accessor.getUser();
            
            if (destination != null && user != null) {
                log.debug("Authorization check for user {} subscribing to {}", user.getName(), destination);
                
                // Check if user is authorized to subscribe to this destination
                if (!isAuthorizedForTopic(destination, user, accessor)) {
                    log.warn("User {} not authorized to subscribe to {}", user.getName(), destination);
                    return null; // Block the subscription
                }
                
                log.debug("User {} authorized for topic {}", user.getName(), destination);
            }
        }
        
        return message;
    }
    
    private boolean isAuthorizedForTopic(String destination, Principal user, StompHeaderAccessor accessor) {
        // Get JWT token from session attributes (set during connect)
        String token = getJwtTokenFromSession(accessor);
        
        // If no token, deny access
        if (token == null) {
            log.warn("No JWT token found for subscription to: {}", destination);
            return false;
        }
        
        // Check if token is expired
        if (JwtUtil.isTokenExpired(token)) {
            log.warn("Expired JWT token used for subscription to: {}", destination);
            return false;
        }
        
        // For simplified authentication - any valid token can access topics
        // No role-based authorization required anymore
        log.debug("Valid token found - allowing access to: {}", destination);
        return true;
    }
    
    private String getJwtTokenFromSession(StompHeaderAccessor accessor) {
        // Try to get token from session attributes (set during connect)
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            return (String) sessionAttributes.get("jwtToken");
        }
        return null;
    }
}