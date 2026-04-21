package com.teamsolution.order.dto.response;

import com.teamsolution.common.core.enums.order.OrderPaymentMethod;
import java.util.UUID;

public record CreateOrderResponse(
    UUID id, String orderNumber, Long total, String paymentUrl, OrderPaymentMethod paymentMethod) {}
