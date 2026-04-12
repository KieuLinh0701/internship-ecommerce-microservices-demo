package com.teamsolution.notification.service.impl;

import com.teamsolution.notification.entity.ProcessedEvent;
import com.teamsolution.notification.repository.ProcessedEventRepository;
import com.teamsolution.notification.service.ProcessedEventService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEventServiceImpl implements ProcessedEventService {

  private final ProcessedEventRepository processedEventRepository;

  @Override
  public boolean isDuplicate(UUID eventId) {
    if (eventId == null) {
      log.warn("[Order] Received event with null eventId, skipping");
      return false;
    }
    if (processedEventRepository.existsById(eventId)) {
      log.warn("[Order] Duplicate event detected, skipping eventId={}", eventId);
      return true;
    }
    return false;
  }

  @Override
  public void markProcessed(UUID eventId) {
    processedEventRepository.save(ProcessedEvent.builder().eventId(eventId).build());
  }
}
