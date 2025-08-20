package com.devwonder.auth_service.service;

import com.devwonder.auth_service.dto.CreatePermissionRequest;
import com.devwonder.auth_service.dto.PermissionDto;
import com.devwonder.auth_service.entity.Permission;
import com.devwonder.auth_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDto> getAllPermissions() {
        log.debug("Fetching all permissions");
        return permissionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PermissionDto> getPermissionById(Long id) {
        log.debug("Fetching permission by id: {}", id);
        return permissionRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<PermissionDto> getPermissionByName(String name) {
        log.debug("Fetching permission by name: {}", name);
        return permissionRepository.findByName(name)
                .map(this::convertToDto);
    }

    public List<PermissionDto> getPermissionsByRoleId(Long roleId) {
        log.debug("Fetching permissions for role id: {}", roleId);
        return permissionRepository.findByRoleId(roleId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        log.info("Creating new permission: {}", request.getName());
        
        if (permissionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Permission with name '" + request.getName() + "' already exists");
        }

        Permission permission = new Permission();
        permission.setName(request.getName());
        
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with id: {}", savedPermission.getId());
        
        return convertToDto(savedPermission);
    }

    @Transactional
    public void deletePermission(Long id) {
        log.info("Deleting permission with id: {}", id);
        
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission with id " + id + " not found"));
        
        // Remove permission from all roles before deleting
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        
        permissionRepository.deleteById(id);
        log.info("Permission deleted successfully");
    }

    @Transactional
    public PermissionDto updatePermission(Long id, CreatePermissionRequest request) {
        log.info("Updating permission with id: {}", id);
        
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission with id " + id + " not found"));
        
        if (!permission.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Permission with name '" + request.getName() + "' already exists");
        }
        
        permission.setName(request.getName());
        Permission updatedPermission = permissionRepository.save(permission);
        
        log.info("Permission updated successfully");
        return convertToDto(updatedPermission);
    }

    public List<Permission> findPermissionsByNames(Set<String> names) {
        return permissionRepository.findByNameIn(names);
    }

    public List<Permission> findPermissionsByIds(List<Long> ids) {
        return permissionRepository.findAllById(ids);
    }

    private PermissionDto convertToDto(Permission permission) {
        return new PermissionDto(permission.getId(), permission.getName());
    }
}