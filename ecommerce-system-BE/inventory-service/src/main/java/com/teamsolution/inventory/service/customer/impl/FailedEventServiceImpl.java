package com.teamsolution.inventory.service.customer.impl;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.config.properties.KafkaConsumerProperties;
import com.teamsolution.common.kafka.event.BaseEvent;
import com.teamsolution.inventory.entity.FailedEvent;
import com.teamsolution.inventory.mapper.FailedEventMapper;
import com.teamsolution.inventory.repository.FailedEventRepository;
import com.teamsolution.inventory.service.customer.FailedEventService;
import com.teamsolution.inventory.specification.FailedEventSpecification;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedEventServiceImpl implements FailedEventService {

  private final FailedEventRepository failedEventRepository;
  private final FailedEventMapper failedEventMapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaConsumerProperties kafkaConsumerProperties;

  @Override
  public Map<FailedEventStatus, Long> getStats() {
    return Arrays.stream(FailedEventStatus.values())
        .collect(Collectors.toMap(status -> status, failedEventRepository::countByStatus));
  }

  @Override
  public Page<FailedEventResponse> getAll(Pageable pageable, FilterFailedEventRequest filter) {

    Specification<FailedEvent> spec = FailedEventSpecification.build(filter);

    Page<FailedEvent> page = failedEventRepository.findAll(spec, pageable);

    return page.map(failedEventMapper::toDto);
  }

  @Override
  @Transactional
  public FailedEventResponse retry(UUID id, UUID adminAccountId) {

    FailedEvent failed = findFailedEventById(id);

    if (failed.getRetryCount() >= failed.getMaxRetry()) {
      throw new AppException(CommonErrorCode.FAILED_EVENT_NOT_RETRYABLE);
    }

    failed.getStatus().validateCanRetry();

    failed.setRetryCount(failed.getRetryCount() + 1);
    failed.setStatus(FailedEventStatus.RETRYING);
    failed.setLastRetryAt(LocalDateTime.now());
    failed.setLastRetriedBy(adminAccountId);
    failedEventRepository.save(failed);

    // Publish the event back to Kafka
    Object event = deserializePayload(failed.getPayloadClass(), failed.getPayload());

    Message<Object> message =
        MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.TOPIC, failed.getTopic())
            .setHeader(KafkaHeaders.KEY, String.valueOf(failed.getAggregateId()))
            .setHeader("eventType", failed.getEventType())
            .setHeader("eventId", failed.getEventId())
            .build();

    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

    future.whenComplete(
        (result, ex) -> {
          if (ex != null) {
            markRepublishFailed(failed, ex.getMessage());
          }
        });

    return failedEventMapper.toDto(failed);
  }

  @Override
  @Transactional
  public FailedEventResponse ignore(UUID id, UUID adminAccountId) {

    FailedEvent failed = findFailedEventById(id);

    failed.getStatus().validateCanIgnore();

    failed.setStatus(FailedEventStatus.DEAD);
    failed.setResolvedAt(LocalDateTime.now());
    failed.setResolvedBy(adminAccountId);

    failedEventRepository.save(failed);

    return failedEventMapper.toDto(failed);
  }

  @Override
  public void saveFailedEvent(ConsumerRecord<?, ?> record, Exception ex) {
    try {
      Header eventTypeHeader = record.headers().lastHeader("eventType");
      String eventType =
          eventTypeHeader != null ? new String(eventTypeHeader.value()) : record.topic();

      UUID eventId;
      if (record.value() instanceof Map<?, ?> map) {
        Object idVal = map.get("id");
        eventId = idVal != null ? UUID.fromString(idVal.toString()) : null;
      } else {
        eventId = ((BaseEvent) record.value()).getId();
      }

      String kafkaKey =
          record.key() instanceof byte[]
              ? new String((byte[]) record.key())
              : String.valueOf(record.key());
      UUID aggregateId = kafkaKey != null ? UUID.fromString(kafkaKey) : null;

      failedEventRepository
          .findByEventId(eventId)
          .ifPresentOrElse(
              f -> {
                if (f.getRetryCount() >= f.getMaxRetry()) {
                  f.setStatus(FailedEventStatus.DEAD);
                } else {
                  f.setStatus(FailedEventStatus.FAILED);
                }
                f.setFailedAt(LocalDateTime.now());
                f.setErrorMessage(ex.getMessage());
                f.setErrorType(
                    ex.getCause() instanceof PermanentException
                        ? FailedEventErrorType.PERMANENT
                        : FailedEventErrorType.TEMPORARY);
                failedEventRepository.save(f);
              },
              () -> {
                failedEventRepository.save(
                    FailedEvent.builder()
                        .topic(record.topic())
                        .eventId(eventId)
                        .aggregateId(aggregateId)
                        .eventType(eventType)
                        .maxRetry(kafkaConsumerProperties.getRetry().getMaxManualRetry())
                        .payload(String.valueOf(JsonUtils.toJson(record.value())))
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
              });
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

  @Override
  public boolean existsByEventId(UUID id) {
    return failedEventRepository.existsByEventId(id);
  }

  private void markRepublishFailed(FailedEvent failed, String errorMessage) {
    failed.setStatus(FailedEventStatus.FAILED);
    failed.setErrorMessage(errorMessage);
    failed.setFailedAt(LocalDateTime.now());
    failed.setErrorType(FailedEventErrorType.TEMPORARY);
    failedEventRepository.save(failed);
  }

  private FailedEvent findFailedEventById(UUID id) {
    return failedEventRepository
        .findById(id)
        .orElseThrow(() -> new AppException(CommonErrorCode.FAILED_EVENT_NOT_FOUND));
  }

  private Object deserializePayload(String payloadClass, String payload) {
    try {
      Class<?> clazz = Class.forName(payloadClass);
      return JsonUtils.fromJson(payload, clazz);
    } catch (ClassNotFoundException e) {
      throw new AppException(CommonErrorCode.UNKNOWN_EVENT_TYPE);
    }
  }
}
