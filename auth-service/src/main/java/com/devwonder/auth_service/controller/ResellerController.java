package com.devwonder.auth_service.controller;

import com.devwonder.auth_service.dto.BaseResponse;
import com.devwonder.auth_service.dto.ResellerRegistrationRequest;
import com.devwonder.auth_service.dto.ResellerRegistrationResponse;
import com.devwonder.auth_service.exception.UsernameAlreadyExistsException;
import com.devwonder.auth_service.exception.RoleNotFoundException;
import com.devwonder.auth_service.service.ResellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/reseller")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reseller Management", description = "APIs for reseller account management")
public class ResellerController {
    
    private final ResellerService resellerService;
    
    @PostMapping("/register")
    @Operation(summary = "Register new reseller account", description = "Create a new reseller account with DEALER role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseller account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<BaseResponse<ResellerRegistrationResponse>> registerReseller(@Valid @RequestBody ResellerRegistrationRequest request) {
        log.info("Received reseller registration request for username: {}", request.getUsername());
        
        try {
            ResellerRegistrationResponse response = resellerService.registerReseller(request);
            BaseResponse<ResellerRegistrationResponse> apiResponse = BaseResponse.success(response, "Reseller account created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
            
        } catch (UsernameAlreadyExistsException e) {
            log.error("Username already exists: {}", e.getMessage());
            BaseResponse<ResellerRegistrationResponse> errorResponse = BaseResponse.error(e.getMessage(), "USERNAME_EXISTS");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            
        } catch (RoleNotFoundException e) {
            log.error("Role not found: {}", e.getMessage());
            BaseResponse<ResellerRegistrationResponse> errorResponse = BaseResponse.error(e.getMessage(), "ROLE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (RuntimeException e) {
            log.error("Runtime error: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("Failed to create reseller profile")) {
                BaseResponse<ResellerRegistrationResponse> errorResponse = BaseResponse.error("Failed to complete registration", "REGISTRATION_FAILED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            BaseResponse<ResellerRegistrationResponse> errorResponse = BaseResponse.error(e.getMessage(), "RUNTIME_ERROR");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            BaseResponse<ResellerRegistrationResponse> errorResponse = BaseResponse.error("An unexpected error occurred", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
}