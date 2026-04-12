package com.teamsolution.search.service.impl;

import com.teamsolution.common.core.dto.admin.failedevent.request.FilterFailedEventRequest;
import com.teamsolution.common.core.dto.admin.failedevent.response.FailedEventResponse;
import com.teamsolution.common.core.enums.failedEvent.FailedEventErrorType;
import com.teamsolution.common.core.enums.failedEvent.FailedEventStatus;
import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.common.core.exception.enums.CommonErrorCode;
import com.teamsolution.common.core.util.JsonUtils;
import com.teamsolution.common.kafka.config.properties.KafkaConsumerProperties;
import com.teamsolution.search.entity.FailedEvent;
import com.teamsolution.search.kafka.dispatcher.FailedEventDispatcher;
import com.teamsolution.search.mapper.FailedEventMapper;
import com.teamsolution.search.repository.FailedEventRepository;
import com.teamsolution.search.service.FailedEventService;
import com.teamsolution.search.specification.FailedEventSpecification;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedEventServiceImpl implements FailedEventService {

  private final FailedEventRepository failedEventRepository;
  private final FailedEventMapper failedEventMapper;
  private final FailedEventDispatcher failedEventDispatcher;
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

    try {
      Object event = deserializePayload(failed.getPayloadClass(), failed.getPayload());
      failedEventDispatcher.dispatch(failed.getTopic(), event, failed.getId());

      failed.setStatus(FailedEventStatus.SUCCESS);
      failed.setResolvedAt(LocalDateTime.now());
      failedEventRepository.save(failed);

    } catch (Exception ex) {
      markRepublishFailed(failed, ex.getMessage());
    }

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
