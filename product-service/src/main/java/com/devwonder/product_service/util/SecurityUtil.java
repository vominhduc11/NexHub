package com.devwonder.product_service.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class SecurityUtil {
    
    public static boolean hasAdminRole(HttpServletRequest request) {
        // Check JWT authorities header from API Gateway
        String authoritiesHeader = request.getHeader("X-JWT-Authorities");
        if (authoritiesHeader != null && !authoritiesHeader.isEmpty()) {
            List<String> authorities = Arrays.asList(authoritiesHeader.split(","));
            return authorities.contains("ROLE_ADMIN");
        }
        
        // Fallback to X-User-Roles header (for backward compatibility)
        String rolesHeader = request.getHeader("X-User-Roles");
        if (rolesHeader == null || rolesHeader.isEmpty()) {
            return false;
        }
        
        try {
            // Decode base64 roles if needed
            String roles = new String(Base64.getDecoder().decode(rolesHeader));
            List<String> rolesList = Arrays.asList(roles.split(","));
            return rolesList.contains("ADMIN");
        } catch (Exception e) {
            // If decoding fails, try as plain text
            List<String> rolesList = Arrays.asList(rolesHeader.split(","));
            return rolesList.contains("ADMIN");
        }
    }
}