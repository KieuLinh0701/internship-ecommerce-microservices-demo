package com.teamsolution.cart.service.internal.impl;

import com.teamsolution.cart.entity.FailedEvent;
import com.teamsolution.cart.repository.FailedEventRepository;
import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.config.properties.KafkaConsumerProperties;
import com.teamsolution.common.kafka.constant.EventPayloadKeys;
import com.teamsolution.common.kafka.constant.KafkaHeaders;
import com.teamsolution.common.kafka.event.BaseEvent;
import com.teamsolution.cart.service.internal.FailedEventSaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedEventSaverServiceImpl implements FailedEventSaverService {

  private final FailedEventRepository failedEventRepository;
  private final KafkaConsumerProperties kafkaConsumerProperties;

  @Override
  public void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex) {
    try {
      Header eventTypeHeader = record.headers().lastHeader(KafkaHeaders.EVENT_TYPE);
      String eventType =
          eventTypeHeader != null ? new String(eventTypeHeader.value()) : record.topic();

      UUID eventId;
      if (record.value() instanceof Map<?, ?> map) {
        Object idVal = map.get(EventPayloadKeys.ID);
        eventId = idVal != null ? UUID.fromString(idVal.toString()) : null;
      } else {
        eventId = ((BaseEvent) record.value()).getId();
      }

      String kafkaKey =
          record.key() instanceof byte[]
              ? new String((byte[]) record.key())
              : String.valueOf(record.key());
      UUID aggregateId = kafkaKey != null ? UUID.fromString(kafkaKey) : null;

      failedEventRepository.save(
          FailedEvent.builder()
              .topic(record.topic())
              .eventId(eventId)
              .aggregateId(aggregateId)
              .eventType(eventType)
              .maxRetry(kafkaConsumerProperties.getRetry().getMaxManualRetry())
              .payload(JsonUtils.toJson(record.value()))
              .payloadClass(record.value().getClass().getName())
              .errorType(
                  ex.getCause() instanceof PermanentException
                      ? FailedEventErrorType.PERMANENT
                      : FailedEventErrorType.TEMPORARY)
              .errorMessage(ex.getMessage())
              .retryCount(0)
              .status(FailedEventStatus.FAILED)
              .failedAt(LocalDateTime.now())
              .build());
    } catch (Exception e) {
      log.error("Failed to save failed event to DB", e);
    }
  }

  @Override
  public void markSuccess(UUID id) {
    FailedEvent failedEvent = findFailedEventById(id);

    if (failedEvent.getStatus() == FailedEventStatus.DEAD) {
      throw new AppException(CommonErrorCode.FAILED_EVENT_NOT_RETRYABLE);
    }

    failedEvent.setStatus(FailedEventStatus.SUCCESS);
    failedEvent.setResolvedAt(LocalDateTime.now());

    failedEventRepository.save(failedEvent);
  }

  private FailedEvent findFailedEventById(UUID id) {
    return failedEventRepository
        .findById(id)
        .orElseThrow(() -> new AppException(CommonErrorCode.FAILED_EVENT_NOT_FOUND));
  }
}
