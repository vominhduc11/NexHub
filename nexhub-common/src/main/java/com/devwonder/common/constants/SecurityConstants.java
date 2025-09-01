package com.devwonder.common.constants;

public final class SecurityConstants {
    
    private SecurityConstants() {}
    
    public static final String GATEWAY_HEADER = "X-Gateway-Request";
    public static final String GATEWAY_SECRET = "nexhub-internal-gateway-2024";
    
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    
    public static final String JWT_SUBJECT_HEADER = "X-JWT-Subject";
    public static final String JWT_USERNAME_HEADER = "X-JWT-Username";
    public static final String JWT_ACCOUNT_ID_HEADER = "X-JWT-Account-ID";
    public static final String JWT_USER_ROLES_HEADER = "X-User-Roles";
    public static final String JWT_USER_PERMISSIONS_HEADER = "X-User-Permissions";
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DEALER = "DEALER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    
    public static final class Permissions {
        public static final String USER_READ = "USER_READ";
        public static final String USER_WRITE = "USER_WRITE";
        public static final String USER_DELETE = "USER_DELETE";
        
        public static final String PRODUCT_READ = "PRODUCT_READ";
        public static final String PRODUCT_WRITE = "PRODUCT_WRITE";
        public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
        
        public static final String BLOG_READ = "BLOG_READ";
        public static final String BLOG_WRITE = "BLOG_WRITE";
        public static final String BLOG_DELETE = "BLOG_DELETE";
        
        public static final String WARRANTY_READ = "WARRANTY_READ";
        public static final String WARRANTY_WRITE = "WARRANTY_WRITE";
        public static final String WARRANTY_DELETE = "WARRANTY_DELETE";
        
        public static final String NOTIFICATION_ACCESS = "NOTIFICATION_ACCESS";
        
        private Permissions() {}
    }
    
    public static final class ApiKeys {
        public static final String AUTH_TO_USER_SERVICE = "AUTH_TO_USER_SERVICE_KEY";
        public static final String USER_TO_AUTH_SERVICE = "USER_TO_AUTH_SERVICE_KEY"; 
        public static final String WARRANTY_TO_USER_SERVICE = "WARRANTY_TO_USER_SERVICE_KEY";
        public static final String WARRANTY_TO_PRODUCT_SERVICE = "WARRANTY_TO_PRODUCT_SERVICE_KEY";
        
        private ApiKeys() {}
    }
}