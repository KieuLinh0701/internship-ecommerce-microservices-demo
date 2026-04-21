package com.teamsolution.payment.service;

import java.util.UUID;

public interface PaymentRefundService {
    void refund(
            UUID orderId,
            String orderNumber,
            UUID customerId,
            String email,
            UUID accountId,
            UUID accountRoleId
    );
}