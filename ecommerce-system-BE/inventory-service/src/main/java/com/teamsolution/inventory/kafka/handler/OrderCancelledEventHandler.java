package com.teamsolution.inventory.kafka.handler;

import com.teamsolution.common.core.enums.order.OrderStatus;
import com.teamsolution.common.core.exception.PermanentException;
import com.teamsolution.common.kafka.event.order.OrderCancelledEvent;
import com.teamsolution.inventory.exception.ErrorCode;
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
public class OrderCancelledEventHandler {

    private static final String EVENT = "OrderCancelledEvent";

    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;
    private final ProductVariantInventoryInternalService inventoryInternalService;

    @Transactional
    public void handle(OrderCancelledEvent event) {
        log.info("[Inventory][{}] Processing orderId={}, previousStatus={}",
                EVENT, event.getOrderId(), event.getPreviousStatus());

        if (event.isPreviousStatus(OrderStatus.CONFIRMED)) {
            inventoryInternalService.restoreConfirmedStock(event.getItems());
        } else if (event.isPreviousStatus(OrderStatus.PENDING)) {
            inventoryInternalService.reserveStock(event.getItems());
        } else {
            throw new PermanentException(
                    ErrorCode.UNHANDLED_ORDER_STATUS,
                    event.getPreviousStatus(),
                    event.getOrderId());
        }

        processedEventService.markProcessed(event.getId());

        log.info("[Inventory][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }

    @Transactional
    public void handleRetry(OrderCancelledEvent event, UUID failedEventId) {
        log.info("[Retry][Inventory][{}] Processing orderId={}", EVENT, event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][Inventory][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }
}
