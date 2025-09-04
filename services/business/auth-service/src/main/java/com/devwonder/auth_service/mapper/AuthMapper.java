package com.devwonder.auth_service.mapper;

import com.devwonder.auth_service.dto.LoginResponse;
import com.devwonder.auth_service.entity.Account;
import com.devwonder.auth_service.entity.Permission;
import com.devwonder.auth_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
public interface AuthMapper {

    default LoginResponse.UserInfo toUserInfo(Account account, String userType) {
        if (account == null) {
            return null;
        }

        // Filter roles by userType
        List<String> roles = account.getRoles().stream()
            .filter(role -> role.getName().equalsIgnoreCase(userType))
            .map(Role::getName)
            .collect(Collectors.toList());

        // Get all permissions from filtered roles
        List<String> permissions = account.getRoles().stream()
            .filter(role -> role.getName().equalsIgnoreCase(userType))
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .collect(Collectors.toList());

        return new LoginResponse.UserInfo(
            account.getId(),
            account.getUsername(),
            userType,
            roles.stream().collect(Collectors.toSet()),
            permissions.stream().collect(Collectors.toSet())
        );
    }
}