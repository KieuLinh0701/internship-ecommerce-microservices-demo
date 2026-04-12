package com.teamsolution.inventory.service.customer.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.kafka.config.properties.OutboxProperties;
import com.teamsolution.common.kafka.enums.OutboxEventStatus;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.repository.OutboxEventRepository;
import com.teamsolution.inventory.service.customer.OutboxEventService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

  private final OutboxEventRepository outboxEventRepository;
  private final OutboxProperties outboxProperties;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markSent(UUID id) {
    OutboxEvent event = findById(id);
    event.setStatus(OutboxEventStatus.SENT);
    event.setSentAt(LocalDateTime.now());
    outboxEventRepository.save(event);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleFailure(UUID id, Throwable cause) {
    OutboxEvent event = findById(id);

    if (event.getStatus() == OutboxEventStatus.SENT) {
      return;
    }

    event.setErrorMessage(cause.getMessage());
    event.setLastRetryAt(LocalDateTime.now());

    if (isPermanentKafkaError(cause)) {
      event.setStatus(OutboxEventStatus.FAILED);

      log.error("Permanent Kafka error event {}: {}", event.getId(), cause.getMessage());
    } else {
      event.setRetryCount(event.getRetryCount() + 1);
      event.setNextRetryAt(
          LocalDateTime.now().plusSeconds(outboxProperties.getRetryDelaySeconds()));
      event.setStatus(OutboxEventStatus.PENDING);
      log.warn("Temporary error event {}, retry later: {}", event.getId(), cause.getMessage());
    }
    outboxEventRepository.save(event);
  }

  private OutboxEvent findById(UUID id) {
    return outboxEventRepository
        .findById(id)
        .orElseThrow(() -> new AppException(CommonErrorCode.OUTBOX_EVENT_NOT_FOUND));
  }

  private boolean isPermanentKafkaError(Throwable cause) {
    return cause instanceof UnknownTopicOrPartitionException
        || cause instanceof InvalidTopicException
        || cause instanceof RecordTooLargeException
        || cause instanceof AppException;
  }
}
