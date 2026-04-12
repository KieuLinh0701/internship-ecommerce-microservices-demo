package com.teamsolution.auth.kafka.producer.impl;

import com.teamsolution.auth.entity.OutboxEvent;
import com.teamsolution.auth.enums.AuthEventType;
import com.teamsolution.auth.kafka.producer.OutboxEventProducer;
import com.teamsolution.auth.service.OutboxEventService;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaProducerHelper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public void publishEvent(OutboxEvent event) {
        String topic = getTopicForEvent(AuthEventType.valueOf(event.getEventType()));
        Object payload =
                parsePayload(AuthEventType.valueOf(event.getEventType()), event.getPayload());

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic,
                null,
                String.valueOf(event.getAggregateId()),
                payload
        );

        KafkaProducerHelper.addTraceHeader(record, event.getTraceId());

        record.headers().add("eventType", event.getEventType().getBytes(StandardCharsets.UTF_8));
        record.headers().add("eventId", event.getId().toString().getBytes(StandardCharsets.UTF_8));

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

    private Object parsePayload(AuthEventType eventType, String payload) {
        return switch (eventType) {
            case OTP_SENT_FOR_PENDING_LOGIN -> JsonUtils.fromJson(payload, AuthNotificationEvent.class);
            case OTP_SENT_FOR_CHANGE_EMAIL -> JsonUtils.fromJson(payload, AuthNotificationEvent.class);
            default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
        };
  }

  private String getTopicForEvent(AuthEventType eventType) {
    return switch (eventType) {
      case AuthEventType.OTP_SENT_FOR_PENDING_LOGIN -> KafkaTopics.AUTH_NOTIFICATION;
      case AuthEventType.OTP_SENT_FOR_CHANGE_EMAIL -> KafkaTopics.AUTH_NOTIFICATION;
      default -> throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    };
  }
}
