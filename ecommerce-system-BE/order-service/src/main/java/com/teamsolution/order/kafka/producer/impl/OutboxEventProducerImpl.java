package com.teamsolution.order.kafka.producer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.order.OrderConfirmedEvent;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.common.kafka.event.order.OrderRefundRequestedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.order.entity.OutboxEvent;
import com.teamsolution.order.enums.OrderEventType;
import com.teamsolution.order.kafka.producer.OutboxEventProducer;
import com.teamsolution.order.service.internal.OutboxEventService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final OutboxEventService outboxEventService;

  @Override
  @Transactional
  public void publishEvent(OutboxEvent event) {
    String topic = getTopicForEvent(OrderEventType.valueOf(event.getEventType()));
    Object payload = parsePayload(OrderEventType.valueOf(event.getEventType()), event.getPayload());

    ProducerRecord<String, Object> record =
        new ProducerRecord<>(topic, null, String.valueOf(event.getAggregateId()), payload);

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

  private Object parsePayload(OrderEventType eventType, String payload) {
    return switch (eventType) {
      case ORDER_CREATED -> JsonUtils.fromJson(payload, OrderCreatedEvent.class);
      case ORDER_CONFIRMED -> JsonUtils.fromJson(payload, OrderConfirmedEvent.class);
      case ORDER_REFUND_REQUESTED -> JsonUtils.fromJson(payload, OrderRefundRequestedEvent.class);
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    };
  }

  private String getTopicForEvent(OrderEventType eventType) {
    return switch (eventType) {
      case ORDER_CREATED -> KafkaTopics.ORDER_CREATED;
      case ORDER_CONFIRMED -> KafkaTopics.ORDER_CONFIRMED;
      case ORDER_REFUND_REQUESTED -> KafkaTopics.ORDER_REFUND_REQUESTED;
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    };
  }
}
