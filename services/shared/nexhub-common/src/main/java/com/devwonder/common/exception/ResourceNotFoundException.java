package com.devwonder.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    
    public ResourceNotFoundException(String resource, String identifier) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with identifier: %s", resource, identifier), 
              HttpStatus.NOT_FOUND.value());
    }
    
    public ResourceNotFoundException(String resource, String field, String identifier) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with %s: %s", resource, field, identifier), 
              HttpStatus.NOT_FOUND.value());
    }
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }
}