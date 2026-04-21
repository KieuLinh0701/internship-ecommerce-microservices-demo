package com.teamsolution.cart.kafka.handler;

import com.teamsolution.cart.service.internal.CartItemInternalService;
import com.teamsolution.cart.service.internal.FailedEventSaverService;
import com.teamsolution.cart.service.internal.ProcessedEventService;
import com.teamsolution.common.kafka.event.inventory.InventoryReservationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationFailedEventHandler {
    private static final String EVENT = "InventoryReservationFailedEvent";

    private final CartItemInternalService cartItemInternalService;
    private final ProcessedEventService processedEventService;
    private final FailedEventSaverService failedEventSaverService;

    @Transactional
    public void handle(InventoryReservationFailedEvent event) {
        log.info("[{}] Handling inventory failure for orderId={}", EVENT, event.getOrderId());

        cartItemInternalService.rollbackCartItems(event.getCartItemIds());

        processedEventService.markProcessed(event.getId());

        log.info("[{}] Completed handling inventory failure for orderId={}", EVENT, event.getOrderId());
    }

    @Transactional
    public void handleRetry(InventoryReservationFailedEvent event, UUID failedEventId) {
        log.info("[Retry][{}] Processing orderId={}", EVENT, event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }

}
