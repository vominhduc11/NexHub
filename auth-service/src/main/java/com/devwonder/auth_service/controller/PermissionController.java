package com.devwonder.auth_service.controller;

import com.devwonder.auth_service.dto.CreatePermissionRequest;
import com.devwonder.auth_service.dto.PermissionDto;
import com.devwonder.auth_service.service.PermissionService;
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
@RequestMapping("/auth/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for managing user permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieve all available permissions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all permissions")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        log.info("Request to get all permissions");
        List<PermissionDto> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission found"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> getPermissionById(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        log.info("Request to get permission by id: {}", id);
        return permissionService.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get permission by name", description = "Retrieve a specific permission by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission found"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> getPermissionByName(
            @Parameter(description = "Permission name") @PathVariable String name) {
        log.info("Request to get permission by name: {}", name);
        return permissionService.getPermissionByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "Get permissions by role ID", description = "Retrieve all permissions assigned to a specific role")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions for role")
    public ResponseEntity<List<PermissionDto>> getPermissionsByRoleId(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        log.info("Request to get permissions for role id: {}", roleId);
        List<PermissionDto> permissions = permissionService.getPermissionsByRoleId(roleId);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping
    @Operation(
        summary = "Create new permission", 
        description = "Create a new permission (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Permission created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or permission already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<PermissionDto> createPermission(
            @Parameter(description = "Permission creation request") @Valid @RequestBody CreatePermissionRequest request) {
        log.info("Request to create new permission: {}", request.getName());
        try {
            PermissionDto createdPermission = permissionService.createPermission(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create permission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update permission", 
        description = "Update an existing permission (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or permission name already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    public ResponseEntity<PermissionDto> updatePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Parameter(description = "Permission update request") @Valid @RequestBody CreatePermissionRequest request) {
        log.info("Request to update permission with id: {}", id);
        try {
            PermissionDto updatedPermission = permissionService.updatePermission(id, request);
            return ResponseEntity.ok(updatedPermission);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update permission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete permission", 
        description = "Delete a permission by its ID (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Permission not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        log.info("Request to delete permission with id: {}", id);
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete permission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}