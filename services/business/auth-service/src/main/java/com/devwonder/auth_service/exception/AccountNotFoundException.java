package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BaseException {
    
    public AccountNotFoundException(String message) {
        super(message, "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }
    
    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId + " not found", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }
}