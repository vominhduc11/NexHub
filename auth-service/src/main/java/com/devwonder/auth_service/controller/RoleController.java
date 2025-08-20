package com.devwonder.auth_service.controller;

import com.devwonder.auth_service.dto.*;
import com.devwonder.auth_service.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve all available roles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        log.info("Request to get all roles");
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve a specific role by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> getRoleById(
        @Parameter(description = "Role ID") @PathVariable Long id) {
        log.info("Request to get role by id: {}", id);
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get role by name", description = "Retrieve a specific role by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> getRoleByName(
            @Parameter(description = "Role name") @PathVariable String name) {
        log.info("Request to get role by name: {}", name);
        return roleService.getRoleByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Create new role", 
        description = "Create a new role (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or role already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<RoleDto> createRole(
            @Parameter(description = "Role creation request") @Valid @RequestBody CreateRoleRequest request) {
        log.info("Request to create new role: {}", request.getName());
        try {
            RoleDto createdRole = roleService.createRole(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update role", 
        description = "Update an existing role (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or role name already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleDto> updateRole(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Parameter(description = "Role update request") @Valid @RequestBody CreateRoleRequest request) {
        log.info("Request to update role with id: {}", id);
        try {
            RoleDto updatedRole = roleService.updateRole(id, request);
            return ResponseEntity.ok(updatedRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete role", 
        description = "Delete a role by its ID (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Role not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        log.info("Request to delete role with id: {}", id);
        try {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Permission management endpoints
    @GetMapping("/with-permissions")
    @Operation(summary = "Get all roles with permissions", description = "Retrieve all roles with their assigned permissions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all roles with permissions")
    public ResponseEntity<List<RoleWithPermissionsDto>> getAllRolesWithPermissions() {
        log.info("Request to get all roles with permissions");
        List<RoleWithPermissionsDto> roles = roleService.getAllRolesWithPermissions();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}/with-permissions")
    @Operation(summary = "Get role with permissions by ID", description = "Retrieve a specific role with its assigned permissions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role with permissions found"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleWithPermissionsDto> getRoleWithPermissions(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        log.info("Request to get role with permissions by id: {}", id);
        RoleWithPermissionsDto role = roleService.getRoleWithPermissions(id);
        return role != null ? ResponseEntity.ok(role) : ResponseEntity.notFound().build();
    }

    @PostMapping("/assign-permissions")
    @Operation(
        summary = "Assign permissions to role", 
        description = "Assign multiple permissions to a role (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permissions assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or role/permission not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<RoleWithPermissionsDto> assignPermissionsToRole(
            @Parameter(description = "Permission assignment request") @Valid @RequestBody AssignPermissionsRequest request) {
        log.info("Request to assign permissions to role: {}", request.getRoleId());
        try {
            RoleWithPermissionsDto updatedRole = roleService.assignPermissionsToRole(request);
            return ResponseEntity.ok(updatedRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to assign permissions: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @Operation(
        summary = "Add permission to role", 
        description = "Add a single permission to a role (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission added successfully"),
        @ApiResponse(responseCode = "400", description = "Role or permission not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<RoleWithPermissionsDto> addPermissionToRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId,
            @Parameter(description = "Permission ID") @PathVariable Long permissionId) {
        log.info("Request to add permission {} to role {}", permissionId, roleId);
        try {
            RoleWithPermissionsDto updatedRole = roleService.addPermissionToRole(roleId, permissionId);
            return ResponseEntity.ok(updatedRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to add permission to role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(
        summary = "Remove permission from role", 
        description = "Remove a permission from a role (Admin only)",
        security = {
            @SecurityRequirement(name = "Gateway Request"),
            @SecurityRequirement(name = "JWT Authentication")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission removed successfully"),
        @ApiResponse(responseCode = "400", description = "Role not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<RoleWithPermissionsDto> removePermissionFromRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId,
            @Parameter(description = "Permission ID") @PathVariable Long permissionId) {
        log.info("Request to remove permission {} from role {}", permissionId, roleId);
        try {
            RoleWithPermissionsDto updatedRole = roleService.removePermissionFromRole(roleId, permissionId);
            return ResponseEntity.ok(updatedRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to remove permission from role: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}