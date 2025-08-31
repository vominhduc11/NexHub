package com.devwonder.warranty_service.exception;

import com.devwonder.common.exception.ResourceNotFoundException;

public class WarrantyClaimNotFoundException extends ResourceNotFoundException {
    public WarrantyClaimNotFoundException(Long claimId) {
        super("Warranty claim", "id", claimId);
    }

    public WarrantyClaimNotFoundException(String claimNumber) {
        super("Warranty claim", "claim number", claimNumber);
    }

    public WarrantyClaimNotFoundException(String fieldName, Object fieldValue) {
        super("Warranty claim", fieldName, fieldValue);
    }
}