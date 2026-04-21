package com.teamsolution.order.kafka.handler;

import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import com.teamsolution.order.service.internal.FailedEventSaverService;
import com.teamsolution.order.service.internal.OrderInternalService;
import com.teamsolution.order.service.internal.ProcessedEventService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationFailedEventHandler {

  private static final String EVENT = "InventoryReservationFailedEvent";

  private final OrderInternalService orderInternalService;
  private final ProcessedEventService processedEventService;
  private final FailedEventSaverService failedEventSaverService;

  @Transactional
  public void handle(InventoryReservationFailedEvent event) {
    log.info("[Order][{}] Handling inventory failure for orderId={}", EVENT, event.getOrderId());

    orderInternalService.handleInventoryFailed(
        event.getOrderId(), event.getAccountId(), event.getAccountRoleId(), event.getCustomerId());

    processedEventService.markProcessed(event.getId());

    log.info("[Order][{}] Completed for orderId={}", EVENT, event.getOrderId());
  }

  @Transactional
  public void handleRetry(InventoryReservationFailedEvent event, UUID failedEventId) {
    log.info("[Retry][Order][{}] Processing orderId={}", EVENT, event.getOrderId());

    handle(event);

    failedEventSaverService.markSuccess(failedEventId);

    log.info("[Retry][Order][{}] Completed for orderId={}", EVENT, event.getOrderId());
  }
}
