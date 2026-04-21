package com.teamsolution.payment.kafka.producer;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentProducer {
    void publishPaymentEvent(
            UUID accountId,
            UUID accountRoleId,
            UUID customerId,
            String email,
            UUID orderId,
            String orderNumber,
            PaymentEventStatus status,
            List<NotificationChannel> channels
    );
}
