package com.teamsolution.auth.kafka.producer.impl;

import com.teamsolution.auth.entity.Account;
import com.teamsolution.auth.entity.OutboxEvent;
import com.teamsolution.auth.enums.AuthEventType;
import com.teamsolution.auth.enums.EntityName;
import com.teamsolution.auth.kafka.producer.NotificationProducer;
import com.teamsolution.auth.repository.OutboxEventRepository;
import com.teamsolution.common.core.enums.notification.NotificationChannel;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.core.util.UuidUtils;
import com.teamsolution.common.kafka.event.notification.AuthNotificationEvent;
import com.teamsolution.tracing.context.TraceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationProducerImpl implements NotificationProducer {

  private final OutboxEventRepository outboxEventRepository;
  private final TraceContext traceContext;

  @Override
  public void send(
      Account account,
      String type,
      List<NotificationChannel> channels,
      Map<String, String> variablesMap,
      AuthEventType eventType,
      EntityName aggregateType) {

    AuthNotificationEvent event =
        AuthNotificationEvent.builder()
            .accountId(account.getId())
            .email(account.getEmail())
            .type(type)
            .channels(channels)
            .variables(variablesMap)
            .build();

    this.saveToOutbox(account.getId(), aggregateType, eventType, event);
  }

  @Override
  public void send(
      String email,
      String type,
      List<NotificationChannel> channels,
      Map<String, String> variablesMap,
      AuthEventType eventType,
      EntityName aggregateType) {

    AuthNotificationEvent event =
        AuthNotificationEvent.builder()
            .accountId(null)
            .email(email)
            .type(type)
            .channels(channels)
            .variables(variablesMap)
            .build();

    this.saveToOutbox(UuidUtils.generate(), aggregateType, eventType, event);
  }

  private void saveToOutbox(
      UUID aggregateId, EntityName aggregateType, AuthEventType eventType, Object payloadEvent) {

    OutboxEvent outboxEvent =
        OutboxEvent.builder()
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
