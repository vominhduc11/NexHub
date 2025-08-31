package com.devwonder.auth_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String roleName) {
        super("Role", "name", roleName);
    }
}