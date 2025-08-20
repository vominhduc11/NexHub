package com.devwonder.user_service.controller;

import com.devwonder.user_service.entity.Admin;
import com.devwonder.user_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Operations related to admin management")
@SecurityRequirement(name = "Gateway Request")
@SecurityRequirement(name = "JWT Authentication")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Get all admins", description = "Retrieve a list of all admins")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved admins"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get admin by account ID", description = "Retrieve a specific admin by account ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin found"),
        @ApiResponse(responseCode = "404", description = "Admin not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Admin> getAdminByAccountId(
            @Parameter(description = "Account ID of the admin to retrieve") @PathVariable Long accountId) {
        Optional<Admin> admin = adminService.getAdminByAccountId(accountId);
        return admin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Admin> getAdminByUsername(@PathVariable String username) {
        Optional<Admin> admin = adminService.getAdminByUsername(username);
        return admin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new admin", description = "Create a new admin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Admin> createAdmin(
            @Parameter(description = "Admin data to create") @RequestBody Admin admin) {
        try {
            Admin createdAdmin = adminService.createAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long accountId, @RequestBody Admin adminDetails) {
        try {
            Admin updatedAdmin = adminService.updateAdmin(accountId, adminDetails);
            return ResponseEntity.ok(updatedAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long accountId) {
        try {
            adminService.deleteAdmin(accountId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{accountId}/exists")
    public ResponseEntity<Boolean> checkAdminExists(@PathVariable Long accountId) {
        boolean exists = adminService.existsByAccountId(accountId);
        return ResponseEntity.ok(exists);
    }
}