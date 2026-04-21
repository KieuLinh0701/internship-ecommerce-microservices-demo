package com.teamsolution.inventory.kafka.handler;

import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
import com.teamsolution.inventory.service.internal.FailedEventSaverService;
import com.teamsolution.inventory.service.internal.ProcessedEventService;
import com.teamsolution.inventory.service.internal.ProductVariantInventoryInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventHandler {

    private static final String EVENT = "OrderCreatedEvent";

    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;
    private final ProductVariantInventoryInternalService inventoryInternalService;

    @Transactional
    public void handle(OrderCreatedEvent event) {
        log.info("[Inventory][{}] Processing for orderId={}", EVENT, event.getOrderId());

        inventoryInternalService.reserveStockForCreateOrder(event);

        processedEventService.markProcessed(event.getId());

        log.info("[Inventory][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }

    @Transactional
    public void handleRetry(OrderCreatedEvent event, UUID failedEventId) {
        log.info("[Retry][Inventory][{}] Processing orderId={}", EVENT, event.getOrderId());

        inventoryInternalService.reserveStock(event.getItems());

        failedEventSaverService.markSuccess(failedEventId);
        processedEventService.markProcessed(event.getId());

        log.info("[Retry][Inventory][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }
}
