package com.devwonder.auth_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.auth_service.dto.LoginRequest;
import com.devwonder.auth_service.dto.LoginResponse;
import com.devwonder.auth_service.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username, password and role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or unauthorized role")
    })
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for username: {} with userType: {}", request.getUsername(), request.getUserType());
        
        try {
            LoginResponse response = authenticationService.login(request);
            BaseResponse<LoginResponse> apiResponse = BaseResponse.success(response, "Login successful");
            return ResponseEntity.ok(apiResponse);
            
        } catch (BadCredentialsException e) {
            log.error("Authentication failed: {}", e.getMessage());
            BaseResponse<LoginResponse> errorResponse = BaseResponse.error(e.getMessage(), "AUTHENTICATION_FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage(), e);
            BaseResponse<LoginResponse> errorResponse = BaseResponse.error("An unexpected error occurred", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}