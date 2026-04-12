package com.teamsolution.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressUpdateRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Phone is required")
        @NotBlank(message = "Phone number is required")
        @Pattern(
            regexp = "^0\\d{9}$",
            message = "Phone number must start with 0 and contain exactly 10 digits")
        String phone,
    @NotNull(message = "City code is required") Integer cityCode,
    @NotBlank(message = "City name is required") String cityName,
    @NotNull(message = "Ward code is required") Integer wardCode,
    @NotBlank(message = "Ward name is required") String wardName,
    @NotBlank(message = "Detail is required") String detail) {}
