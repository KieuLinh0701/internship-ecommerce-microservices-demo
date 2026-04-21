package com.teamsolution.cart.kafka.handler;

import com.teamsolution.cart.service.internal.CartItemInternalService;
import com.teamsolution.cart.service.internal.FailedEventSaverService;
import com.teamsolution.cart.service.internal.ProcessedEventService;
import com.teamsolution.common.kafka.event.order.OrderCreatedEvent;
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
    private final CartItemInternalService cartItemInternalService;

    @Transactional
    public void handle(OrderCreatedEvent event) {
        log.info("[{}] Processing for orderId={}", EVENT, event.getOrderId());

        cartItemInternalService.checkoutCartItems(event.getCartItemIds());

        processedEventService.markProcessed(event.getId());

        log.info("[{}] Completed for orderId={}", EVENT, event.getOrderId());
    }


    @Transactional
    public void handleRetry(OrderCreatedEvent event, UUID failedEventId) {
        log.info("[Retry][{}] Processing orderId={}", EVENT, event.getOrderId());

        handle(event);

        failedEventSaverService.markSuccess(failedEventId);

        log.info("[Retry][{}] Completed for orderId={}", EVENT, event.getOrderId());
    }
}
