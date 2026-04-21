package com.teamsolution.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AvailableCouponRequest(
    @NotNull(message = "Order Value is required") @Min(0) Long orderValue,
    @NotNull(message = "Shipping Fee is required") @Min(0) Long shippingFee,
    UUID couponId) {}
