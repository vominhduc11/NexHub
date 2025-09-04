package com.devwonder.auth_service.enums;

/**
 * Account status enumeration for multi-role account management
 * Handles different account states across all user types
 */
public enum AccountStatus {
    /**
     * Account is pending approval - for accounts that require approval process
     * Users cannot login with pending accounts
     */
    PENDING,
    
    /**
     * Account is approved and active - can be used for authentication
     * This is the normal operational state
     */
    APPROVED,
    
    /**
     * Account is rejected - approval was denied
     * Users cannot login with rejected accounts
     */
    REJECTED
}