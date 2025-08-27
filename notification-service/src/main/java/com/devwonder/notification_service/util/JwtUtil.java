package com.devwonder.notification_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Slf4j
public class JwtUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Extract username from JWT token without verification
     * Note: In production, you should verify the token signature
     */
    public static String extractUsername(String token) {
        try {
            // Split JWT token (header.payload.signature)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT token format");
                return null;
            }
            
            // Decode payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse JSON payload
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            // Extract username
            String username = (String) claims.get("username");
            if (username == null) {
                // Fallback to 'sub' claim if username not present
                username = (String) claims.get("sub");
            }
            
            log.debug("Extracted username from JWT: {}", username);
            return username;
            
        } catch (Exception e) {
            log.error("Failed to extract username from JWT token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract user type from JWT token
     */
    public static String extractUserType(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            return (String) claims.get("userType");
            
        } catch (Exception e) {
            log.error("Failed to extract userType from JWT token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public static List<String> extractRoles(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Collections.emptyList();
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            List<String> roles = (List<String>) claims.get("roles");
            return roles != null ? roles : Collections.emptyList();
            
        } catch (Exception e) {
            log.error("Failed to extract roles from JWT token: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Extract permissions from JWT token
     */
    @SuppressWarnings("unchecked")
    public static List<String> extractPermissions(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Collections.emptyList();
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            List<String> permissions = (List<String>) claims.get("permissions");
            return permissions != null ? permissions : Collections.emptyList();
            
        } catch (Exception e) {
            log.error("Failed to extract permissions from JWT token: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles.contains(role);
    }
    
    /**
     * Check if user has any of the specified roles
     */
    public static boolean hasAnyRole(String token, String... roles) {
        List<String> userRoles = extractRoles(token);
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has specific permission
     */
    public static boolean hasPermission(String token, String permission) {
        List<String> permissions = extractPermissions(token);
        return permissions.contains(permission);
    }
    
    /**
     * Check if JWT token is expired (basic check without verification)
     */
    public static boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return true;
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            Long exp = ((Number) claims.get("exp")).longValue();
            long currentTime = System.currentTimeMillis() / 1000;
            
            return exp < currentTime;
            
        } catch (Exception e) {
            log.error("Failed to check token expiration: {}", e.getMessage());
            return true; // Consider expired if cannot parse
        }
    }
}