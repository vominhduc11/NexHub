package com.devwonder.api_gateway.constants;

public final class RateLimitConstants {
    
    private RateLimitConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final int MAX_REQUESTS_PER_MINUTE = 300;
    public static final long WINDOW_SIZE_MILLIS = 60 * 1000L; // 1 minute in milliseconds
    public static final String RETRY_AFTER_HEADER_VALUE = "60"; // seconds
}