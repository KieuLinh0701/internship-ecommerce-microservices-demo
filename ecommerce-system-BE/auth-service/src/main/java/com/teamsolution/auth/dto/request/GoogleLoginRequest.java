package com.teamsolution.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
    String picture,
    @NotBlank(message = "Name is required") String name) {}
