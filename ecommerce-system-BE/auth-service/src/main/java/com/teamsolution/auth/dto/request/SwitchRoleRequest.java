package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SwitchRoleRequest(
    @NotBlank(message = "Role is required") String role,
    @NotBlank(message = "Refresh Token is required") String refreshToken) {}
