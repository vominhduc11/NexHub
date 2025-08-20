package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.*;
import com.devwonder.auth_service.entity.Permission;
import com.devwonder.auth_service.entity.Role;
import com.devwonder.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public List<RoleDto> getAllRoles() {
        log.debug("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<RoleDto> getRoleById(Long id) {
        log.debug("Fetching role by id: {}", id);
        return roleRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<RoleDto> getRoleByName(String name) {
        log.debug("Fetching role by name: {}", name);
        return roleRepository.findByName(name)
                .map(this::convertToDto);
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        log.info("Creating new role: {}", request.getName());
        
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        Role role = new Role();
        role.setName(request.getName());
        
        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with id: {}", savedRole.getId());
        
        return convertToDto(savedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);
        
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role with id " + id + " not found");
        }
        
        roleRepository.deleteById(id);
        log.info("Role deleted successfully");
    }

    @Transactional
    public RoleDto updateRole(Long id, CreateRoleRequest request) {
        log.info("Updating role with id: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + id + " not found"));
        
        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }
        
        role.setName(request.getName());
        Role updatedRole = roleRepository.save(role);
        
        log.info("Role updated successfully");
        return convertToDto(updatedRole);
    }

    public RoleWithPermissionsDto getRoleWithPermissions(Long id) {
        log.debug("Fetching role with permissions by id: {}", id);
        return roleRepository.findById(id)
                .map(this::convertToRoleWithPermissionsDto)
                .orElse(null);
    }

    public List<RoleWithPermissionsDto> getAllRolesWithPermissions() {
        log.debug("Fetching all roles with permissions");
        return roleRepository.findAll().stream()
                .map(this::convertToRoleWithPermissionsDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleWithPermissionsDto assignPermissionsToRole(AssignPermissionsRequest request) {
        log.info("Assigning permissions to role id: {}", request.getRoleId());
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + request.getRoleId() + " not found"));
        
        List<Permission> permissions = permissionService.findPermissionsByIds(request.getPermissionIds());
        
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new IllegalArgumentException("One or more permission IDs are invalid");
        }
        
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        
        Role savedRole = roleRepository.save(role);
        log.info("Successfully assigned {} permissions to role", permissions.size());
        
        return convertToRoleWithPermissionsDto(savedRole);
    }

    @Transactional
    public RoleWithPermissionsDto addPermissionToRole(Long roleId, Long permissionId) {
        log.info("Adding permission {} to role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + roleId + " not found"));
        
        Permission permission = permissionService.findPermissionsByIds(List.of(permissionId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Permission with id " + permissionId + " not found"));
        
        role.getPermissions().add(permission);
        Role savedRole = roleRepository.save(role);
        
        log.info("Successfully added permission to role");
        return convertToRoleWithPermissionsDto(savedRole);
    }

    @Transactional
    public RoleWithPermissionsDto removePermissionFromRole(Long roleId, Long permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with id " + roleId + " not found"));
        
        role.getPermissions().removeIf(permission -> permission.getId().equals(permissionId));
        Role savedRole = roleRepository.save(role);
        
        log.info("Successfully removed permission from role");
        return convertToRoleWithPermissionsDto(savedRole);
    }

    private RoleDto convertToDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }

    private RoleWithPermissionsDto convertToRoleWithPermissionsDto(Role role) {
        List<PermissionDto> permissionDtos = role.getPermissions().stream()
                .map(permission -> new PermissionDto(permission.getId(), permission.getName()))
                .collect(Collectors.toList());
        
        return new RoleWithPermissionsDto(role.getId(), role.getName(), permissionDtos);
    }
}