package com.devwonder.auth_service.mapper;

import com.devwonder.auth_service.dto.LoginResponse;
import com.devwonder.auth_service.dto.ResellerRegistrationRequest;
import com.devwonder.auth_service.dto.ResellerRegistrationResponse;
import com.devwonder.auth_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    
    @Mapping(target = "id", source = "account.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "userType", source = "userType")
    @Mapping(target = "roles", source = "account.roles", qualifiedByName = "rolesToStringList")
    @Mapping(target = "permissions", source = "account.roles", qualifiedByName = "permissionsToStringSet")
    LoginResponse.UserInfo toUserInfo(Account account, String userType);
    
    @Named("rolesToStringList")
    default java.util.Set<String> rolesToStringList(java.util.Set<com.devwonder.auth_service.entity.Role> roles) {
        return roles.stream()
                .map(com.devwonder.auth_service.entity.Role::getName)
                .collect(java.util.stream.Collectors.toSet());
    }
    
    @Named("permissionsToStringSet")
    default java.util.Set<String> permissionsToStringSet(java.util.Set<com.devwonder.auth_service.entity.Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(com.devwonder.auth_service.entity.Permission::getName)
                .collect(java.util.stream.Collectors.toSet());
    }
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "message", constant = "Registration successful")
    ResellerRegistrationResponse toResellerRegistrationResponse(ResellerRegistrationRequest request);
}