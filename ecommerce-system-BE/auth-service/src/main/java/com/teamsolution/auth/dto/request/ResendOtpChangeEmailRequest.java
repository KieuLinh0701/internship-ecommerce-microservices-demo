package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpChangeEmailRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format")
        String newEmail) {}
