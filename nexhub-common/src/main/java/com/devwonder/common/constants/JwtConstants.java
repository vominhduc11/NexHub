package com.devwonder.common.constants;

public final class JwtConstants {
    
    private JwtConstants() {}
    
    public static final String ISSUER = "nexhub-auth-service";
    public static final String AUDIENCE = "nexhub-services";
    
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_USER_TYPE = "userType";
    public static final String CLAIM_ACCOUNT_ID = "accountId";
    
    public static final String ALGORITHM = "RS256";
    public static final String KEY_TYPE = "RSA";
    public static final String KEY_USE = "sig";
    
    public static final long DEFAULT_EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24 hours
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 days
    
    public static final String JWKS_ENDPOINT = "/.well-known/jwks.json";
    public static final String DEFAULT_JWKS_URI = "http://auth-service:8081/auth/.well-known/jwks.json";
}