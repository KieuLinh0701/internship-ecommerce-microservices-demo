package com.teamsolution.inventory.scheduler;

import com.teamsolution.common.kafka.enums.OutboxEventStatus;
import com.teamsolution.inventory.entity.OutboxEvent;
import com.teamsolution.inventory.kafka.producer.OutboxEventProducer;
import com.teamsolution.inventory.repository.OutboxEventRepository;
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

  private static final String SCHEDULER = "OutboxEventScheduler";

  private final OutboxEventRepository outboxEventRepository;
  private final OutboxEventProducer outboxEventProducer;

  @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms}")
  public void retryPendingEvents() {

    long start = System.currentTimeMillis();

    log.info("[{}] START retryPendingEvents | time={}", SCHEDULER, LocalDateTime.now());

    List<OutboxEvent> pendingEvents =
        outboxEventRepository.findTop100ByStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
            OutboxEventStatus.PENDING, LocalDateTime.now());

    if (pendingEvents.isEmpty()) {
      log.debug("[{}] No pending events", SCHEDULER);
      return;
    }

    log.info("[{}] Found {} pending events", SCHEDULER, pendingEvents.size());

    int success = 0;
    int failed = 0;

    for (OutboxEvent event : pendingEvents) {

      try {
        log.info(
            "[{}] Publishing event | id={} | type={}",
            SCHEDULER,
            event.getId(),
            event.getEventType());

        event.setStatus(OutboxEventStatus.IN_PROGRESS);
        outboxEventRepository.save(event);
        outboxEventProducer.publishEvent(event);

        success++;
      } catch (Exception e) {
        failed++;
        log.error(
            "[{}] Failed to publish outbox event id={}: message={}",
            SCHEDULER,
            event.getId(),
            e.getMessage());
      }
    }

    long duration = System.currentTimeMillis() - start;

    log.info(
        "[{}] END retryPendingEvents | total={} | success={} | failed={} | durationMs={}",
        SCHEDULER,
        pendingEvents.size(),
        success,
        failed,
        duration);
  }
}
