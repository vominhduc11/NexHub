package com.devwonder.auth_service.controller;

import com.devwonder.auth_service.dto.AccountDto;
import com.devwonder.auth_service.dto.CreateAccountRequest;
import com.devwonder.auth_service.dto.UpdatePasswordRequest;
import com.devwonder.auth_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing user accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(
        summary = "Get all accounts", 
        description = "Retrieve all accounts (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        log.info("Request to get all accounts");
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get account by ID", 
        description = "Retrieve a specific account by its ID (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<AccountDto> getAccountById(
            @Parameter(description = "Account ID") @PathVariable Long id) {
        log.info("Request to get account by id: {}", id);
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @Operation(
        summary = "Get account by username", 
        description = "Retrieve a specific account by its username (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<AccountDto> getAccountByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        log.info("Request to get account by username: {}", username);
        return accountService.getAccountByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Create new account", 
        description = "Create a new user account (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or username already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<AccountDto> createAccount(
            @Parameter(description = "Account creation request") @Valid @RequestBody CreateAccountRequest request) {
        log.info("Request to create new account: {}", request.getUsername());
        try {
            AccountDto createdAccount = accountService.createAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create account: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/password")
    @Operation(
        summary = "Update account password", 
        description = "Update password for an account (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or current password incorrect"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountDto> updatePassword(
            @Parameter(description = "Account ID") @PathVariable Long id,
            @Parameter(description = "Password update request") @Valid @RequestBody UpdatePasswordRequest request) {
        log.info("Request to update password for account with id: {}", id);
        try {
            AccountDto updatedAccount = accountService.updatePassword(id, request);
            return ResponseEntity.ok(updatedAccount);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update password: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete account", 
        description = "Delete an account by its ID (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Account not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account ID") @PathVariable Long id) {
        log.info("Request to delete account with id: {}", id);
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete account: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}