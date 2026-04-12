package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotBlank(message = "Password is required")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message =
                "Password must be at least 8 characters long and include at least one uppercase letter, "
                    + "one lowercase letter, one number, and one special character")
        String password,
    @NotBlank(message = "Full name is required") String fullName,
    @NotBlank(message = "Phone number is required")
        @Pattern(
            regexp = "^0\\d{9}$",
            message = "Phone number must start with 0 and contain exactly 10 digits")
        String phone) {}
