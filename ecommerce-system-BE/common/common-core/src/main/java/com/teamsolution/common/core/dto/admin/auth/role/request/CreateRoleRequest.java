package com.teamsolution.common.core.dto.admin.auth.role.request;

import com.teamsolution.common.core.enums.auth.RoleStatus;
import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
    @NotBlank(message = "Name is required") String name, RoleStatus status) {}
