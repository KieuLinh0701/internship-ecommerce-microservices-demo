package com.teamsolution.auth.scheduler;

import com.teamsolution.auth.entity.OutboxEvent;
import com.teamsolution.auth.kafka.producer.OutboxEventProducer;
import com.teamsolution.auth.repository.OutboxEventRepository;
import com.teamsolution.common.kafka.enums.OutboxEventStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {

  private final OutboxEventRepository outboxEventRepository;
  private final OutboxEventProducer outboxEventProducer;

  @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms}")
  public void retryPendingEvents() {

    log.info("[OutboxScheduler] Starting retry of pending events at {}", LocalDateTime.now());

    List<OutboxEvent> pendingEvents =
        outboxEventRepository.findTop100ByStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
            OutboxEventStatus.PENDING, LocalDateTime.now());

    if (pendingEvents.isEmpty()) {
      log.debug("[OutboxScheduler] No pending events");
      return;
    }

    log.info("[OutboxScheduler] Found {} pending events", pendingEvents.size());

    for (OutboxEvent event : pendingEvents) {

      try {
        log.info(
            "[OutboxScheduler] Publishing event id={}, type={}",
            event.getId(),
            event.getEventType());

        event.setStatus(OutboxEventStatus.IN_PROGRESS);
        outboxEventRepository.save(event);
        outboxEventProducer.publishEvent(event);
      } catch (Exception e) {
        log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage());
      }
    }

    log.info("[OutboxScheduler] Finished processing {} events", pendingEvents.size());
  }
}
