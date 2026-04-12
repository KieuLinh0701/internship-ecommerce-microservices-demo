package com.teamsolution.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemQuantityRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be at least 0")
        int quantity
) {
}