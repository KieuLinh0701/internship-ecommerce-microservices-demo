package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyChangeEmailRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format")
        String newEmail,
    @NotBlank(message = "OTP is required") String otp) {}
