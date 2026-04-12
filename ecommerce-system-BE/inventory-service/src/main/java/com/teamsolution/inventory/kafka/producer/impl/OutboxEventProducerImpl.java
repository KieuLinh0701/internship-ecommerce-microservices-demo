package com.teamsolution.inventory.kafka.producer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.inventory.ProductChangedEvent;
import com.teamsolution.common.kafka.event.inventory.ProductStatusChangedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.tracing.utils.TraceUtils;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.enums.InventoryEventType;
import com.teamsolution.inventory.kafka.producer.OutboxEventProducer;
import com.teamsolution.inventory.service.customer.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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

        var builder =
                MessageBuilder.withPayload(payload)
                        .setHeader(KafkaHeaders.TOPIC, topic)
                        .setHeader(KafkaHeaders.KEY, String.valueOf(event.getAggregateId()))
                        .setHeader("eventType", event.getEventType())
                        .setHeader("eventId", event.getId());

        if (event.getTraceId() != null) {
            builder.setHeader("traceparent",
                    "00-" + event.getTraceId() + "-" + TraceUtils.generateSpanId() + "-01");
        }

        Message<Object> message = builder.build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

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
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        };
    }

    private String getTopicForEvent(InventoryEventType eventType) {
        return switch (eventType) {
            case InventoryEventType.CREATE_PRODUCT, UPDATE_PRODUCT -> KafkaTopics.PRODUCT_CHANGED;
            case InventoryEventType.DELETE_PRODUCT, RESTORE_PRODUCT -> KafkaTopics.PRODUCT_STATUS_CHANGED;
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        };
    }
}
