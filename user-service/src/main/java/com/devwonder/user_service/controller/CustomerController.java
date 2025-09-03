package com.devwonder.user_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.user_service.dto.CreateCustomerRequest;
import com.devwonder.user_service.dto.CustomerResponse;
import com.devwonder.user_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "APIs for creating customer accounts")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "Create customer profile", description = "Create a new customer profile with personal information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Customer already exists")
    })
    public ResponseEntity<BaseResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        log.info("Received create customer request for account ID: {}", request.getAccountId());
        
        try {
            CustomerResponse response = customerService.createCustomer(request);
            return ResponseUtil.created("Customer created successfully", response);
            
        } catch (BaseException e) {
            log.error("Error creating customer: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage(), e.getErrorCode(), e.getHttpStatus());
        }
    }

    @Operation(summary = "Check if customer exists by account ID")
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsById(@PathVariable Long accountId) {
        boolean exists = customerService.existsById(accountId);
        return ResponseUtil.success("Customer existence checked successfully", exists);
    }
}