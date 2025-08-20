package com.devwonder.auth_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionsRequest {

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotEmpty(message = "Permission IDs list cannot be empty")
    private List<Long> permissionIds;
}