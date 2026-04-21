package com.teamsolution.payment.kafka.producer.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.enums.PaymentEventStatus;
import com.teamsolution.common.kafka.event.payment.PaymentEvent;
import com.teamsolution.common.tracing.context.TraceContext;
import com.teamsolution.payment.entity.OutboxEvent;
import com.teamsolution.payment.enums.EntityName;
import com.teamsolution.payment.kafka.producer.PaymentProducer;
import com.teamsolution.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentProducerImpl
        implements PaymentProducer {

    private final OutboxEventRepository outboxEventRepository;
    private final TraceContext traceContext;

    @Override
    public void publishPaymentEvent(
            UUID accountId,
            UUID accountRoleId,
            UUID customerId,
            String email,
            UUID orderId,
            String orderNumber,
            PaymentEventStatus status,
            List<NotificationChannel> channels) {

        PaymentEvent event = PaymentEvent.builder()
                .accountId(accountId)
                .accountRoleId(accountRoleId)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .channels(channels)
                .build();

        this.saveToOutbox(orderId, status, event);
    }

    private void saveToOutbox(
            UUID aggregateId,
            PaymentEventStatus status,
            Object payloadEvent) {

        EntityName aggregateType = null;
        switch (status) {
            case PAYMENT_COMPLETED, PAYMENT_FAILED -> aggregateType = EntityName.PAYMENT;
            case REFUND_COMPLETED, REFUND_FAILED -> aggregateType = EntityName.PAYMENT_REFUND;
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .traceId(traceContext.currentTraceId())
                .aggregateId(aggregateId)
                .aggregateType(aggregateType.getValue())
                .eventType(status.name())
                .nextRetryAt(LocalDateTime.now())
                .payload(JsonUtils.toJson(payloadEvent))
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
