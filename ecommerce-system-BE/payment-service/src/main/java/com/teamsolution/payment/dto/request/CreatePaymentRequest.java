package com.teamsolution.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        Long amount,

        @NotNull(message = "Payment method is required")
        String method
) {}