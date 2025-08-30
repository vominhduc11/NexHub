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
import java.util.Map;

@Slf4j
public class WebSocketAuthorizationInterceptor implements ChannelInterceptor {

    
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
        
        // Validate JWT token (signature + expiration)
        if (!JwtUtil.validateToken(token, "your-secret-key-here")) {
            log.warn("Invalid JWT token used for subscription to: {}", destination);
            return false;
        }
        
        // Allow access for any valid token holder (no role restrictions)
        String userType = JwtUtil.extractUserType(token);
        log.debug("User {} with role {} authorized to subscribe to {}", user.getName(), userType, destination);
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