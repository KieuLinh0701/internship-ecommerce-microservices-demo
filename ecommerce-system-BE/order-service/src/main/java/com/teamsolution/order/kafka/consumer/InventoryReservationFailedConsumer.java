package com.teamsolution.order.kafka.consumer;

import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.common.kafka.topics.KafkaTopics;
import com.teamsolution.common.kafka.utils.KafkaTracingUtils;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.ProcessedEventService;
import io.opentelemetry.api.OpenTelemetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationFailedConsumer {

  private final OrderInternalService orderInternalService;
  private final ProcessedEventService processedEventService;
  private final OpenTelemetry openTelemetry;

  @KafkaListener(
      topics = KafkaTopics.INVENTORY_RESERVATION_FAILED,
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  public void handle(ConsumerRecord<String, InventoryReservationFailedEvent> record) {
    KafkaTracingUtils.run(
        openTelemetry,
        record,
        () -> {
          InventoryReservationFailedEvent event = record.value();

          log.info(
              "[Order] Received InventoryReservationFailedEvent eventId={}, orderId={}",
              event.getId(),
              event.getOrderId());

          if (processedEventService.isDuplicate(event.getId())) {
            return;
          }

          log.info("[Order] Handling inventory failure for orderId={}", event.getOrderId());

          orderInternalService.handleInventoryFailed(
              event.getOrderId(),
              event.getAccountId(),
              event.getAccountRoleId(),
              event.getCustomerId());

          processedEventService.markProcessed(event.getId());

          log.info(
              "[Order] Completed handling inventory failure for orderId={}", event.getOrderId());
        });
  }
}
