package com.devwonder.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for security-related operations
 */
@Slf4j
public class SecurityUtil {
    
    private static final String GATEWAY_HEADER = "X-Gateway-Request";
    private static final String GATEWAY_VALUE = "true";
    private static final String USER_ROLE_HEADER = "X-User-Roles";
    private static final String ADMIN_ROLE = "ADMIN";
    
    /**
     * Private constructor to prevent instantiation
     */
    private SecurityUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Check if the current user has ADMIN role
     * @param request HTTP request containing role information
     * @return true if user has ADMIN role, false otherwise
     */
    public static boolean hasAdminRole(HttpServletRequest request) {
        String userRoles = request.getHeader(USER_ROLE_HEADER);
        
        if (userRoles == null || userRoles.trim().isEmpty()) {
            log.warn("Access denied - No roles found in request headers");
            return false;
        }
        
        // Check if ADMIN role is in the comma-separated list of roles
        boolean hasAdmin = userRoles.contains(ADMIN_ROLE);
        
        if (!hasAdmin) {
            log.warn("Access denied - Current roles: '{}', Required: '{}'", userRoles, ADMIN_ROLE);
        }
        
        return hasAdmin;
    }
    
    /**
     * Check if the request comes from the API Gateway
     * @param request HTTP request to check
     * @return true if request comes from gateway, false otherwise
     */
    public static boolean isGatewayRequest(HttpServletRequest request) {
        String gatewayHeader = request.getHeader(GATEWAY_HEADER);
        boolean isFromGateway = GATEWAY_VALUE.equals(gatewayHeader);
        
        if (!isFromGateway) {
            log.warn("Access denied - Request not from API Gateway. Header value: '{}'", gatewayHeader);
        }
        
        return isFromGateway;
    }
    
    /**
     * Get the current user roles from request headers
     * @param request HTTP request
     * @return comma-separated user roles or null if not found
     */
    public static String getUserRoles(HttpServletRequest request) {
        return request.getHeader(USER_ROLE_HEADER);
    }
}