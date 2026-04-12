package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank(message = "Token is required") String resetToken,
    @NotBlank(message = "Password is required") String password) {}
