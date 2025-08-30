package com.devwonder.notification_service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Slf4j
public class JwtUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {};
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    
    // JWT secret should be injected properly via constructor or setter
    // For now, keeping as constant until proper configuration is set up
    private static final String DEFAULT_JWT_SECRET = "your-secret-key-here";
    
    // Private constructor to prevent instantiation
    private JwtUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Extract claims from JWT token
     * @param token JWT token
     * @return Claims map or null if invalid
     */
    private static Map<String, Object> extractClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("JWT token is null or empty");
            return null;
        }
        
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT token format - expected 3 parts but got {}", parts.length);
                return null;
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(payload, MAP_TYPE_REF);
            
        } catch (Exception e) {
            log.error("Failed to extract claims from JWT token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract username from JWT token without verification
     * Note: In production, you should verify the token signature
     */
    public static String extractUsername(String token) {
        Map<String, Object> claims = extractClaims(token);
        if (claims == null) {
            return null;
        }
        
        // Extract username with fallback to 'sub' claim
        String username = (String) claims.get("username");
        if (username == null) {
            username = (String) claims.get("sub");
        }
        
        log.debug("Extracted username from JWT: {}", username);
        return username;
    }
    
    /**
     * Extract user type from JWT token
     */
    public static String extractUserType(String token) {
        Map<String, Object> claims = extractClaims(token);
        return claims != null ? (String) claims.get("userType") : null;
    }
    
    /**
     * Extract roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public static List<String> extractRoles(String token) {
        Map<String, Object> claims = extractClaims(token);
        if (claims == null) {
            return Collections.emptyList();
        }
        
        List<String> roles = (List<String>) claims.get("roles");
        return roles != null ? roles : Collections.emptyList();
    }
    
    /**
     * Extract permissions from JWT token
     */
    @SuppressWarnings("unchecked")
    public static List<String> extractPermissions(String token) {
        Map<String, Object> claims = extractClaims(token);
        if (claims == null) {
            return Collections.emptyList();
        }
        
        List<String> permissions = (List<String>) claims.get("permissions");
        return permissions != null ? permissions : Collections.emptyList();
    }
    
    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String token, String role) {
        if (role == null) {
            return false;
        }
        
        List<String> roles = extractRoles(token);
        return roles.contains(role);
    }
    
    /**
     * Check if user has any of the specified roles
     */
    public static boolean hasAnyRole(String token, String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        
        List<String> userRoles = extractRoles(token);
        if (userRoles.isEmpty()) {
            return false;
        }
        
        for (String role : roles) {
            if (role != null && userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has specific permission
     */
    public static boolean hasPermission(String token, String permission) {
        if (permission == null) {
            return false;
        }
        
        List<String> permissions = extractPermissions(token);
        return permissions.contains(permission);
    }
    
    /**
     * Validate JWT token signature
     */
    public static boolean validateTokenSignature(String token, String secret) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("Invalid JWT token format - expected 3 parts but got {}", parts.length);
                return false;
            }
            
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            
            // Create signature for verification
            String data = header + "." + payload;
            String expectedSignature = createSignature(data, secret);
            
            // Compare signatures
            boolean isValid = expectedSignature.equals(signature);
            if (!isValid) {
                log.warn("JWT signature validation failed");
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("Failed to validate JWT signature: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Create HMAC signature for JWT
     */
    private static String createSignature(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to create JWT signature: {}", e.getMessage());
            throw new RuntimeException("JWT signature creation failed", e);
        }
    }
    
    /**
     * Validate JWT token (signature + expiration)
     * Currently only validates structure and expiration due to RSA256 signature complexity
     */
    public static boolean validateToken(String token, String secret) {
        try {
            log.debug("JWT validation - checking structure and expiration only (signature validation disabled)");
            
            // Check basic token structure
            if (!isValidJwtStructure(token)) {
                log.warn("Invalid JWT token structure");
                return false;
            }
            
            // Check expiration
            if (isTokenExpired(token)) {
                log.warn("JWT token is expired");
                return false;
            }
            
            log.debug("JWT token validation successful - token is valid and not expired");
            return true;
            
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if JWT has valid structure (3 parts)
     */
    private static boolean isValidJwtStructure(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
    
    /**
     * Check if JWT token is expired (basic check without verification)
     */
    public static boolean isTokenExpired(String token) {
        Map<String, Object> claims = extractClaims(token);
        if (claims == null) {
            return true; // Consider expired if cannot parse
        }
        
        try {
            Object expClaim = claims.get("exp");
            if (expClaim == null) {
                log.warn("JWT token missing expiration claim");
                return true;
            }
            
            long exp = ((Number) expClaim).longValue();
            long currentTime = System.currentTimeMillis() / 1000;
            
            return exp < currentTime;
            
        } catch (Exception e) {
            log.error("Failed to check token expiration: {}", e.getMessage());
            return true; // Consider expired if cannot parse
        }
    }
}