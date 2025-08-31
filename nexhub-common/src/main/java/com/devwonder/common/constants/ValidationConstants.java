package com.devwonder.common.constants;

public final class ValidationConstants {
    
    private ValidationConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // Text field length constraints
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_EXCERPT_LENGTH = 1000;
    public static final int MAX_META_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_META_KEYWORDS_LENGTH = 1000;
    public static final int MAX_WARRANTY_TEXT_LENGTH = 1000;
    public static final int MAX_USE_CASES_LENGTH = 1000;
    public static final int MAX_ACCESSORIES_LENGTH = 1000;
    
    // Issue description constraints
    public static final int MIN_ISSUE_DESCRIPTION_LENGTH = 10;
    public static final int MAX_ISSUE_DESCRIPTION_LENGTH = 1000;
}