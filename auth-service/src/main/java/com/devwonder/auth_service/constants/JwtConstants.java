package com.devwonder.auth_service.constants;

public final class JwtConstants {
    
    private JwtConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final int RSA_KEY_SIZE = 2048;
    public static final int EXPIRATION_TIME_MULTIPLIER = 1000; // Convert seconds to milliseconds
}