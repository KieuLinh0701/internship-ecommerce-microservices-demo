package com.teamsolution.inventory.kafka.producer.impl;

import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.tracing.context.TraceContext;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.enums.EntityName;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.kafka.producer.InventoryProducer;
import com.teamsolution.inventory.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryProducerImpl
        implements InventoryProducer {

    private final OutboxEventRepository outboxEventRepository;
    private final TraceContext traceContext;

    @Override
    public void publishInventoryReservationFailedEvent(
            UUID accountId,
            UUID accountRoleId,
            UUID customerId,
            String email,
            UUID orderId,
            String orderNumber,
            List<NotificationChannel> channelList,
            List<UUID> cartItemIds) {

        InventoryReservationFailedEvent event = InventoryReservationFailedEvent.builder()
                .accountId(accountId)
                .accountRoleId(accountRoleId)
                .customerId(customerId)
                .email(email)
                .orderId(orderId)
                .orderNumber(orderNumber)
                .channels(channelList)
                .cartItemIds(cartItemIds)
                .build();

        this.saveToOutbox(orderId, EntityName.PRODUCT_VARIANT_INVENTORY, InventoryEventType.INVENTORY_RESERVATION_FAILED, event);
    }

    private void saveToOutbox(
            UUID aggregateId,
            EntityName aggregateType,
            InventoryEventType eventType,
            Object payloadEvent) {

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .traceId(traceContext.currentTraceId())
                .aggregateId(aggregateId)
                .aggregateType(aggregateType.getValue())
                .eventType(eventType.name())
                .nextRetryAt(LocalDateTime.now())
                .payload(JsonUtils.toJson(payloadEvent))
                .build();

        outboxEventRepository.save(outboxEvent);
    }

}
