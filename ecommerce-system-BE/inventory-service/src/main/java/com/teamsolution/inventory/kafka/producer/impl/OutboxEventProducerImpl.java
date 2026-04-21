package com.teamsolution.inventory.kafka.producer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.kafka.producer.OutboxEventProducer;
import com.teamsolution.inventory.service.internal.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventProducerImpl
        implements OutboxEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void publishEvent(OutboxEvent event) {
        String topic = getTopicForEvent(InventoryEventType.valueOf(event.getEventType()));
        Object payload =
                parsePayload(InventoryEventType.valueOf(event.getEventType()), event.getPayload());

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic,
                null,
                String.valueOf(event.getAggregateId()),
                payload
        );

        KafkaTracingUtils.addTraceHeader(record, event.getTraceId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);

        future.whenComplete(
                (result, ex) -> {
                    if (ex != null) {
                        outboxEventService.handleFailure(event.getId(), ex);
                    } else {
                        outboxEventService.markSent(event.getId());
                    }
                });
    }

    private Object parsePayload(InventoryEventType eventType, String payload) {
        return switch (eventType) {
            case InventoryEventType.CREATE_PRODUCT, UPDATE_PRODUCT ->
                    JsonUtils.fromJson(payload, ProductChangedEvent.class);
            case InventoryEventType.DELETE_PRODUCT, RESTORE_PRODUCT ->
                    JsonUtils.fromJson(payload, ProductStatusChangedEvent.class);
            case InventoryEventType.INVENTORY_RESERVATION_FAILED ->
                    JsonUtils.fromJson(payload, OrderCreatedEvent.class);
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        };
    }

    private String getTopicForEvent(InventoryEventType eventType) {
        return switch (eventType) {
            case InventoryEventType.CREATE_PRODUCT, UPDATE_PRODUCT -> KafkaTopics.PRODUCT_CHANGED;
            case InventoryEventType.DELETE_PRODUCT, RESTORE_PRODUCT -> KafkaTopics.PRODUCT_STATUS_CHANGED;
            case InventoryEventType.INVENTORY_RESERVATION_FAILED ->  KafkaTopics.INVENTORY_RESERVATION_FAILED;
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        };
    }
}
