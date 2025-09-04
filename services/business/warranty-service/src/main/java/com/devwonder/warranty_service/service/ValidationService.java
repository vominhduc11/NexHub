package com.devwonder.warranty_service.service;

import com.devwonder.warranty_service.client.ProductServiceClient;
import com.devwonder.warranty_service.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {
    
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    
    public void validatePurchasedProductReferences(Long productSerialId, Long resellerId, Long customerId) {
        log.info("Validating references - ProductSerial: {}, Reseller: {}, Customer: {}", 
                productSerialId, resellerId, customerId);
        
        // Validate Product Serial exists
        try {
            Boolean productSerialExists = productServiceClient.productSerialExists(productSerialId);
            if (!Boolean.TRUE.equals(productSerialExists)) {
                throw new IllegalArgumentException("Product serial with ID " + productSerialId + " does not exist");
            }
        } catch (Exception e) {
            log.error("Error validating product serial {}: {}", productSerialId, e.getMessage());
            throw new IllegalArgumentException("Unable to validate product serial with ID " + productSerialId, e);
        }
        
        // Validate Reseller exists
        try {
            Boolean resellerExists = userServiceClient.resellerExists(resellerId);
            if (!Boolean.TRUE.equals(resellerExists)) {
                throw new IllegalArgumentException("Reseller with ID " + resellerId + " does not exist");
            }
        } catch (Exception e) {
            log.error("Error validating reseller {}: {}", resellerId, e.getMessage());
            throw new IllegalArgumentException("Unable to validate reseller with ID " + resellerId, e);
        }
        
        // Validate Customer exists
        try {
            Boolean customerExists = userServiceClient.customerExists(customerId);
            if (!Boolean.TRUE.equals(customerExists)) {
                throw new IllegalArgumentException("Customer with ID " + customerId + " does not exist");
            }
        } catch (Exception e) {
            log.error("Error validating customer {}: {}", customerId, e.getMessage());
            throw new IllegalArgumentException("Unable to validate customer with ID " + customerId, e);
        }
        
        log.info("All references validated successfully");
    }
}