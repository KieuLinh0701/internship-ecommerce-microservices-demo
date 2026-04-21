package com.teamsolution.order.dto.request;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "Address is required") UUID addressId,
    @NotNull(message = "Payment method is required") OrderPaymentMethod paymentMethod,
    @Size(max = 2, message = "Only 2 coupons are allowed") List<UUID> couponIds,
    String notes,
    @NotEmpty(message = "CartItem must have at least 1 item") List<UUID> cartItemIds,
    @NotNull(message = "Expected total is required")
        @Min(value = 0, message = "Expected total must be at least 0")
        Long expectedTotal,
    @NotNull(message = "Expected discount amount is required")
        @Min(value = 0, message = "Expected discount amount must be at least 0")
        Long expectedDiscountAmount,
    @NotNull(message = "Expected shipping fee is required")
        @Min(value = 0, message = "Expected shipping fee must be at least 0")
        Long expectedDiscountShippingFee,
    @NotNull(message = "Expected subtotal is required")
        @Min(value = 0, message = "Expected subtotal must be at least 0")
        Long expectedSubtotal) {}
