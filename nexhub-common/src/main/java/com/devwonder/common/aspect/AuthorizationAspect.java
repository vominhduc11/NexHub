package com.devwonder.common.aspect;

import com.devwonder.common.annotation.RequireAdminRole;
import com.devwonder.common.annotation.RequireGatewayRequest;
import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect to handle authorization annotations
 */
@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    /**
     * Handle @RequireAdminRole annotation
     */
    @Around("@annotation(requireAdminRole)")
    public Object handleAdminRoleRequired(ProceedingJoinPoint joinPoint, RequireAdminRole requireAdminRole) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        
        if (request == null) {
            log.error("Cannot get HTTP request from context");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Internal server error", "INTERNAL_ERROR"));
        }
        
        // Check if user has admin role
        if (!SecurityUtil.hasAdminRole(request)) {
            log.warn("Access denied - ADMIN role required for method: {}", 
                joinPoint.getSignature().getName());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error(requireAdminRole.message(), requireAdminRole.errorCode()));
        }
        
        // Proceed with the original method execution
        return joinPoint.proceed();
    }
    
    /**
     * Handle @RequireGatewayRequest annotation
     */
    @Around("@annotation(requireGatewayRequest)")
    public Object handleGatewayRequestRequired(ProceedingJoinPoint joinPoint, RequireGatewayRequest requireGatewayRequest) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        
        if (request == null) {
            log.error("Cannot get HTTP request from context");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Internal server error", "INTERNAL_ERROR"));
        }
        
        // Check if request comes from gateway
        if (!SecurityUtil.isGatewayRequest(request)) {
            log.warn("Access denied - Gateway request required for method: {}", 
                joinPoint.getSignature().getName());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error(requireGatewayRequest.message(), requireGatewayRequest.errorCode()));
        }
        
        // Proceed with the original method execution
        return joinPoint.proceed();
    }
    
    /**
     * Get current HTTP request from Spring's RequestContextHolder
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}