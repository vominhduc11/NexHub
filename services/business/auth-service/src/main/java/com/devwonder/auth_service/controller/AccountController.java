package com.devwonder.auth_service.controller;

import com.devwonder.common.dto.BaseResponse;
import com.devwonder.common.exception.BaseException;
import com.devwonder.common.util.ResponseUtil;
import com.devwonder.auth_service.dto.CreateAccountRequest;
import com.devwonder.auth_service.dto.CreateAccountResponse;
import com.devwonder.auth_service.service.AccountService;
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
@RequestMapping("/auth/account")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Management", description = "Internal APIs for account management")
public class AccountController {
    
    private final AccountService accountService;
    
    @PostMapping
    @Operation(summary = "Create new account", description = "Internal API to create a new account (called by other services)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<BaseResponse<CreateAccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @RequestHeader("X-API-Key") String apiKey) throws BaseException {
        
        log.info("Received internal account creation request for username: {}", request.getUsername());
        
        CreateAccountResponse response = accountService.createAccount(request);
        return ResponseUtil.created("Account created successfully", response);
    }
    
    @DeleteMapping("/{accountId}")
    @Operation(summary = "Delete account for compensation", description = "Internal API to delete account for compensation rollback (called by other services)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<BaseResponse<String>> deleteAccount(
            @PathVariable Long accountId,
            @RequestHeader("X-API-Key") String apiKey) throws BaseException {
        
        log.info("Received internal account deletion request for account ID: {}", accountId);
        
        accountService.deleteAccount(accountId);
        return ResponseUtil.success("Account deleted successfully", "Account with ID " + accountId + " has been deleted");
    }
}