package com.devwonder.auth_service.enums;

/**
 * Account type enumeration to identify the account category
 * Used to determine approval requirements and default status
 */
public enum AccountType {
    /**
     * Administrator account - automatically approved
     */
    ADMIN,
    
    /**
     * Customer account - automatically approved  
     */
    CUSTOMER,
    
    /**
     * Dealer/Reseller account - requires approval process
     */
    DEALER
}